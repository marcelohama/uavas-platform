package simulators.uavastsim;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.ConeShape;
import com.bulletphysics.collision.shapes.SphereShape;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import simulators.uavastsim.TSimUAVAS;

public class UFOModel {

	public RigidBody ufoBody = null;
	private CompoundShape bodyCompound = null;

	private DynamicsWorld ownerWorld;
	private CollisionShape shapes[] = new CollisionShape[3];

	public CollisionShape[] getShapes() {
		return shapes;
	}

	public UFOModel(DynamicsWorld ownerWorld,
			Vector3f positionOffset, TSimUAVAS sim) {
		this.ownerWorld = ownerWorld;

		Transform offset = new Transform();
		Transform trans = new Transform();
		Transform tmpTrans = new Transform();
		Transform loadTrans = new Transform();

		offset.setIdentity();
		offset.origin.set(new Vector3f(0f, 0f, 0f));

		// creates the main compound shape for the uav's model
		bodyCompound = new CompoundShape();

		// creates the body and the components
		shapes[0] = new ConeShape(5f, 1f);
		shapes[1] = new ConeShape(5f, 1f);
		shapes[2] = new SphereShape(1.75f);

		loadTrans.setIdentity();
		loadTrans.origin.set(new Vector3f(0f, 1f, 0f));

		trans.setIdentity();
		Quat4f q = new Quat4f();
		QuaternionUtil.setRotation(q, new Vector3f(1.0f,
				0.0f, 0.0f), BulletGlobals.SIMD_PI);
		trans.setRotation(q);
		tmpTrans.mul(offset, trans);
		ufoBody = localCreateRigidBody(1, tmpTrans,
				bodyCompound);
		//bodyCompound.addChildShape(tmpTrans, shapes[0]);

		//bodyCompound.addChildShape(loadTrans, shapes[1]);
		bodyCompound.addChildShape(loadTrans, shapes[2]);

		offset.setIdentity();
		offset.origin.set(new Vector3f(positionOffset));
		ufoBody.setWorldTransform(offset);

		ufoBody.setMassProps(500, new Vector3f(0f, 0f, 0f));
	}

	public void destroy() {
		ownerWorld.removeRigidBody(ufoBody);
		ufoBody.destroy();
		ufoBody = null;
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

}
