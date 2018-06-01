package mind.arch;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSyntax.Literal;
import jason.infra.centralised.RunCentralisedMAS;

import java.util.ArrayList;
import java.util.List;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.ConeShape;
import com.bulletphysics.collision.shapes.CylinderShape;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import simulators.uavastsim.TSimEnvironment;
import simulators.uavastsim.TSimUAVAS;

import mind.engine.GeoPoint;

public class UAVASMKQuadrotor extends AgArch {

	public static final int NUM_LEGS = 4;
	private static final int NUM_MEMBERS = 4;
	private static final int BODYPART_COUNT = 4 * NUM_LEGS + 1;
	private static final int JOINT_COUNT = BODYPART_COUNT - 1;

	public boolean stopped = true;
	public GeoPoint curPoint = null;
	public TSimEnvironment env = null;
	public boolean patrolMode = false;

	private List<GeoPoint> path = new ArrayList<GeoPoint>();
	private GeoPoint home = null;
	private int curPointIndex = 0;
	public float batteryLevel = 100;

	public RigidBody uavBody = null;
	public CompoundShape bodyCompound = null;
	public Vector3f loadStartPos = new Vector3f();

	private DynamicsWorld ownerWorld;
	private CollisionShape[] shapes = new CollisionShape[BODYPART_COUNT + 1];
	private TypedConstraint[] joints = new TypedConstraint[JOINT_COUNT];

	public Vector3f orientation = new Vector3f(0f, 0f, 0f);

	public float collectiveForce; // control the blades's forces
	public float cyclicForce; // control the inclination throught up and down
	public float tailForce; // control the rotation throught the sides
	public float rollForce; // control the rotation on sides

	public Vector3f curPosition;

	public CollisionShape[] getShapes() {
		return shapes;
	}

	public UAVASMKQuadrotor() {
	}

	public UAVASMKQuadrotor(DynamicsWorld ownerWorld,
			Vector3f positionOffset, int id, TSimUAVAS sim) {
		this.ownerWorld = ownerWorld;
		curPointIndex = -1;

		curPosition = positionOffset;

		collectiveForce = 0;
		cyclicForce = 0;
		tailForce = 0;
		rollForce = 0;

		// setup of geometry
		float bodySize = 0.25f;
		float armLength = 0.85f;
		float footLength = 0.2f;
		float propellerAxis = 0.15f;
		float propellerRadius = 0.45f;

		Transform offset = new Transform();
		offset.setIdentity();
		offset.origin.set(new Vector3f(0f, 0f, 0f));

		// creates the main compound shape for the uav's model
		bodyCompound = new CompoundShape();

		// creates the body and the components
		shapes[0] = new CapsuleShape(bodySize, 0.05f);
		for (int i = 0; i < NUM_LEGS; i++) {
			shapes[1 + NUM_MEMBERS * i] = new CapsuleShape(
					0.05f, armLength);
			shapes[2 + NUM_MEMBERS * i] = new CapsuleShape(
					0.1f, footLength);
			shapes[3 + NUM_MEMBERS * i] = new ConeShape(
					0.05f, propellerAxis);
			shapes[4 + NUM_MEMBERS * i] = new CylinderShape(
					new Vector3f(propellerRadius, 0.001f,
							4f));
		}
		Transform loadTrans = new Transform();
		loadTrans.setIdentity();
		loadTrans.origin.set(new Vector3f(0f, 0f, 0f));
		uavBody = localCreateRigidBody(1, loadTrans,
				bodyCompound);
		bodyCompound.addChildShape(loadTrans, shapes[0]);

		for (int i = 0; i < NUM_LEGS; i++) {
			float angle = BulletGlobals.SIMD_2_PI * i
					/ NUM_LEGS;
			float sin = (float) Math.sin(angle);
			float cos = (float) Math.cos(angle);

			Transform trans = new Transform();
			Transform tmpTrans = new Transform();
			trans.setIdentity();
			trans.origin.set(new Vector3f(cos
					* (bodySize + 0.5f * armLength), 0, sin
					* (bodySize + 0.5f * armLength)));
			// arm
			Vector3f toBone = new Vector3f(cos
					* (bodySize + 0.5f * armLength), 0, sin
					* (bodySize + 0.5f * armLength));
			Vector3f axis = new Vector3f();
			axis.cross(toBone, new Vector3f(0.0f, 1.0f,
					0.0f));
			Quat4f q = new Quat4f();
			QuaternionUtil.setRotation(q, axis,
					BulletGlobals.SIMD_HALF_PI);
			trans.setRotation(q);
			tmpTrans.mul(offset, trans);
			bodyCompound.addChildShape(tmpTrans, shapes[1
					+ NUM_MEMBERS * i]);
			// foot
			trans.setIdentity();
			trans.origin.set(cos * (bodySize), -0.5f
					* footLength, sin * (bodySize));
			tmpTrans.mul(offset, trans);
			bodyCompound.addChildShape(tmpTrans, shapes[2
					+ NUM_MEMBERS * i]);
			// propeller axis
			trans.setIdentity();
			trans.origin.set(cos * (bodySize + armLength),
					0.5f * propellerAxis, sin
							* (bodySize + armLength));
			tmpTrans.mul(offset, trans);
			bodyCompound.addChildShape(tmpTrans, shapes[3
					+ NUM_MEMBERS * i]);
			// propeller
			trans.setIdentity();
			trans.origin.set(cos * (bodySize + armLength),
					0.5f * propellerAxis, sin
							* (bodySize + armLength));
			tmpTrans.mul(offset, trans);
			bodyCompound.addChildShape(tmpTrans, shapes[4
					+ NUM_MEMBERS * i]);
		}
		offset.setIdentity();
		offset.origin.set(new Vector3f(curPosition));
		uavBody.setWorldTransform(offset);

		// creates the agent internal MAS and an ID for the agent in the
		// simulator
		RunCentralisedMAS
				.main(new String[] { "uavastsim.mas2j" });
		env = (TSimEnvironment) RunCentralisedMAS
				.getRunner().getAg("uavas_tsim")
				.getEnvInfraTier().getUserEnvironment();
		env.addPercept(Literal.parseLiteral("myId(" + id
				+ ")"));
		env.sim = sim;
		// sets home position
		Vector3f v = uavBody
				.getWorldTransform(new Transform()).origin;
		env.removePerceptsByUnif(Literal
				.parseLiteral("home(_,_,_)"));
		env.addPercept(Literal.parseLiteral("home(" + -v.x
				+ "," + v.z + "," + v.y + ")"));
	}

	public void destroy() {
		int i;
		ownerWorld.removeRigidBody(uavBody);
		uavBody.destroy();
		uavBody = null;
		for (i = 0; i < JOINT_COUNT; ++i) {
			ownerWorld.removeConstraint(joints[i]);
			joints[i] = null;
		}
		for (i = 0; i < BODYPART_COUNT; ++i) {
			shapes[i] = null;
		}
	}

	private RigidBody localCreateRigidBody(float mass,
			Transform startTransform, CollisionShape shape) {
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f();
		localInertia.set(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}
		DefaultMotionState myMotionState = new DefaultMotionState(
				startTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
				mass, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		ownerWorld.addRigidBody(body);
		return body;
	}

	public TypedConstraint[] getJoints() {
		return joints;
	}

	public void addWayPoint(GeoPoint wp) {
		path.add(new GeoPoint(-wp.getLongitude(), wp
				.getHeight(), wp.getLatitute()));
	}

	public GeoPoint getNextPoint() {
		if (path.size() > curPointIndex + 1) {
			curPointIndex++;
			curPoint = path.get(curPointIndex);
			return path.get(curPointIndex);
		} else if (patrolMode && path.size() > 0) {
			curPointIndex = 0;
			curPoint = path.get(curPointIndex);
			return path.get(curPointIndex);
		}
		return null;
	}

	// === Methods for UAVAS
	public GeoPoint getLocation() {
		Vector3f v = uavBody
				.getWorldTransform(new Transform()).origin;
		return new GeoPoint(v.z, -v.x, v.y);
	}

	public void clearPath() {
		path.clear();
		curPointIndex = -1;
		stopped = true;
		curPoint = null;
		patrolMode = false;
		uavBody.setLinearVelocity(new Vector3f(0f, 0f, 0f));
	}

	public void setHome(GeoPoint home) {
		this.home = home;
	}

	public GeoPoint getHome() {
		return home;
	}

	public void doPatrol() {
		patrolMode = true;
	}

	public float getBattery() {
		return batteryLevel;
	}

	public void messageHandler(int idFrom, String message,
			char type) {
		if (type == 'R') {
			env.removePerceptsByUnif(Literal
					.parseLiteral("lastRequest(_,_)"));
			env.addPercept(Literal
					.parseLiteral("lastRequest(" + idFrom
							+ "," + message + ")"));
		} else if (type == 'A') {
			env.removePerceptsByUnif(Literal
					.parseLiteral("lastAsk(_,_)"));
			env.addPercept(Literal.parseLiteral("lastAsk("
					+ idFrom + "," + message + ")"));
		} else if (type == 'I') {
			env.removePerceptsByUnif(Literal
					.parseLiteral("lastInform(_,_)"));
			env.addPercept(Literal
					.parseLiteral("lastInform(" + idFrom
							+ "," + message + ")"));
		} else if (type == 'K') {
			env.removePerceptsByUnif(Literal
					.parseLiteral("lastAck(_,_)"));
			env.addPercept(Literal.parseLiteral("lastAck("
					+ idFrom + "," + message + ")"));
		}
	}

	public void moveTo(GeoPoint p) {
		// stops me, before doing anything...
		stopped = true;
		path.clear();
		// add a new waypoint
		addWayPoint(p);
		curPointIndex = -1;
	}

	// === Methods for UAVAS

	@Override
	public void act(ActionExec action, List<ActionExec> feedback) {
		super.act(action, feedback);
	}

}
