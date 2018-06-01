package simulators.uavastsim;

import com.bulletphysics.util.ObjectArrayList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.OptimizedBvh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.vecmath.Vector3f;

import mind.arch.UAVASMKQuadrotor;
import opengl.DemoApplication;
import opengl.GLDebugDrawer;
import opengl.IGL;
import opengl.LWJGL;

import org.lwjgl.LWJGLException;

import static opengl.IGL.*;

public class TSimUAVAS extends DemoApplication {

	private static final boolean TEST_SERIALIZATION = false;
	private static final boolean SERIALIZE_TO_DISK = true;
	private final float AT_DESTINY_OFFSET = 0.5f;
	private final float VQ = 1;

	private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	private TriangleIndexVertexArray indexVertexArrays;
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;

	private static ByteBuffer gVertices;
	private static ByteBuffer gIndices;
	private static BvhTriangleMeshShape trimeshShape;
	private static RigidBody staticBody;
	private static float waveheight = 2.0f;

	private static final float TRIANGLE_SIZE = 1.f;
	private static int NUM_VERTS_X = 50;
	private static int NUM_VERTS_Y = 100;
	private static int totalVerts = NUM_VERTS_X
			* NUM_VERTS_Y;

	public static TSimUAVAS instance;

	public TSimUAVAS(IGL gl) {
		super(gl);
		instance = this;
	}

	public ObjectArrayList<UAVASMKQuadrotor> uavs = new ObjectArrayList<UAVASMKQuadrotor>();
	public ObjectArrayList<UFOModel> ufos = new ObjectArrayList<UFOModel>();

	// this allocates the position of land's vertex
	public void setVertexPositions(float waveheight,
			float offset) {
		int i;
		int j;
		Vector3f tmp = new Vector3f();

		for (i = 0; i < NUM_VERTS_X; i++) {
			for (j = 0; j < NUM_VERTS_Y; j++) {
				tmp.set((i - NUM_VERTS_X * 0.5f)
						* TRIANGLE_SIZE,
						waveheight
								* (float) Math
										.sin((float) i
												+ offset)
								* (float) Math
										.cos((float) j
												+ offset),
						(j - NUM_VERTS_Y * 0.5f)
								* TRIANGLE_SIZE);

				int index = i + j * NUM_VERTS_X;
				gVertices.putFloat((index * 3 + 0) * 4,
						tmp.x);
				gVertices.putFloat((index * 3 + 1) * 4,
						tmp.y);
				gVertices.putFloat((index * 3 + 2) * 4,
						tmp.z);
			}
		}
	}

	private Transform createLandscape() {
		BulletGlobals
				.setContactAddedCallback(new CustomMaterialCombinerCallback());

		int vertStride = 3 * 4;
		int indexStride = 3 * 4;
		int totalTriangles = 2 * (NUM_VERTS_X - 1)
				* (NUM_VERTS_Y - 1);

		gVertices = ByteBuffer.allocateDirect(
				totalVerts * 3 * 4).order(
				ByteOrder.nativeOrder());
		gIndices = ByteBuffer.allocateDirect(
				totalTriangles * 3 * 4).order(
				ByteOrder.nativeOrder());
		setVertexPositions(waveheight, 0.f);

		gIndices.clear();
		for (int i = 0; i < NUM_VERTS_X - 1; i++) {
			for (int j = 0; j < NUM_VERTS_Y - 1; j++) {
				gIndices.putInt(j * NUM_VERTS_X + i);
				gIndices.putInt(j * NUM_VERTS_X + i + 1);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i
						+ 1);

				gIndices.putInt(j * NUM_VERTS_X + i);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i
						+ 1);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i);
			}
		}
		gIndices.flip();

		indexVertexArrays = new TriangleIndexVertexArray(
				totalTriangles, gIndices, indexStride,
				totalVerts, gVertices, vertStride);

		boolean useQuantizedAabbCompression = true;

		if (TEST_SERIALIZATION) {
			if (SERIALIZE_TO_DISK) {
				trimeshShape = new BvhTriangleMeshShape(
						indexVertexArrays,
						useQuantizedAabbCompression);
				collisionShapes.add(trimeshShape);
				try {
					ObjectOutputStream out = new ObjectOutputStream(
							new GZIPOutputStream(
									new FileOutputStream(
											new File(
													"bvh.bin"))));
					out.writeObject(trimeshShape
							.getOptimizedBvh());
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				trimeshShape = new BvhTriangleMeshShape(
						indexVertexArrays,
						useQuantizedAabbCompression, false);
				OptimizedBvh bvh = null;
				try {
					ObjectInputStream in = new ObjectInputStream(
							new GZIPInputStream(
									new FileInputStream(
											new File(
													"bvh.bin"))));
					bvh = (OptimizedBvh) in.readObject();
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				trimeshShape.setOptimizedBvh(bvh);
				trimeshShape.recalcLocalAabb();
			}
		} else {
			trimeshShape = new BvhTriangleMeshShape(
					indexVertexArrays,
					useQuantizedAabbCompression);
			collisionShapes.add(trimeshShape);
		}
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(
				collisionConfiguration);
		broadphase = new DbvtBroadphase();
		solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(
				dispatcher, broadphase, solver,
				collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0f, /*-9.8f*/
				0f, 0f));
		dynamicsWorld.setDebugDrawer(new GLDebugDrawer(gl));

		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(0f, -2f, 0f);

		return startTransform;
	}

	public void spawnMKQuadrotor(Vector3f startOffset) {
		UAVASMKQuadrotor uav = new UAVASMKQuadrotor(
				dynamicsWorld, new Vector3f(-startOffset.y,
						startOffset.z, startOffset.x),
				uavs.size() + 1, this);
		uavs.add(uav);
	}

	public void spawnUFO(Vector3f startOffset) {
		UFOModel ufo = new UFOModel(dynamicsWorld,
				new Vector3f(-startOffset.y, startOffset.z,
						startOffset.x), this);
		ufos.add(ufo);
	}

	public void initPhysics() {
		Transform startTransform = createLandscape();
		CollisionShape groundShape = trimeshShape;
		startTransform.setIdentity();
		staticBody = localCreateRigidBody(0.f,
				startTransform, groundShape);
		staticBody.setCollisionFlags(staticBody
				.getCollisionFlags()
				| CollisionFlags.STATIC_OBJECT);
		staticBody.setCollisionFlags(staticBody
				.getCollisionFlags()
				| CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
		setCameraDistance(30);
	}

	@Override
	public synchronized void clientMoveAndDisplay() {
		gl.glClear(GL_COLOR_BUFFER_BIT
				| GL_DEPTH_BUFFER_BIT);
		float dt = getDeltaTimeMicroseconds() * 0.000001f;
		ufos.get(0).ufoBody
				.setAngularVelocity(new Vector3f(0f, 1f, 0f));
		for (int r = 0; r < uavs.size(); r++) {
			UAVASMKQuadrotor uav = uavs.get(r);
			uav.uavBody
					.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
			if (uav.batteryLevel <= 0) {
				uav.uavBody.applyCentralForce(new Vector3f(
						0, -9.8f, 0));
				uav.uavBody.setLinearVelocity(new Vector3f(
						0f, 0f, 0f));
				uav.stopped = true;
			} else if (uav.stopped == true) {
				uav.getNextPoint(); // take the next point
				if (uav.curPoint != null)
					uav.stopped = false;
			} else if (uav.curPoint != null) {
				uav.batteryLevel = uav.batteryLevel - 0.01f;
				// get each axis distance difference
				float dx = uav.curPoint.getLatitute()
						- uav.uavBody
								.getWorldTransform(new Transform()).origin.x;
				float dy = uav.curPoint.getLongitude()
						- uav.uavBody
								.getWorldTransform(new Transform()).origin.y;
				float dz = uav.curPoint.getHeight()
						- uav.uavBody
								.getWorldTransform(new Transform()).origin.z;
				// calculate each angle
				double b = Math.atan(dy / dx);
				double dh = dy / Math.sin(b);
				double a = Math.atan(dz / dh);
				// calculate and set each speed vector
				float Vz = (float) (Math.sin(a) * VQ);
				if (dz * Vz < 0)
					Vz *= -1;
				float Vy = (float) (Math.sin(b)
						* Math.cos(a) * VQ);
				if (dy * Vy < 0)
					Vy *= -1;
				float Vx = (float) (Math.cos(b)
						* Math.cos(a) * VQ);
				if (dx * Vx < 0)
					Vx *= -1;
				uav.uavBody.applyCentralForce(new Vector3f(
						Vx, Vy, Vz));

				// reached at waypoint
				if (Math.abs(dx) < AT_DESTINY_OFFSET
						&& Math.abs(dy) < AT_DESTINY_OFFSET
						&& Math.abs(dz) < AT_DESTINY_OFFSET) {
					uav.uavBody
							.setLinearVelocity(new Vector3f(
									0f, 0f, 0f));
					uav.stopped = true;
				}
			}
		}
		dynamicsWorld.stepSimulation(dt);
		dynamicsWorld.debugDrawWorld();
		renderme();
	}

	@Override
	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT
				| GL_DEPTH_BUFFER_BIT);
		renderme();
		if (dynamicsWorld != null) {
			dynamicsWorld.debugDrawWorld();
		}
	}

	private static float calculateCombinedFriction(
			float friction0, float friction1) {
		float friction = friction0 * friction1;

		float MAX_FRICTION = 10f;
		if (friction < -MAX_FRICTION) {
			friction = -MAX_FRICTION;
		}
		if (friction > MAX_FRICTION) {
			friction = MAX_FRICTION;
		}
		return friction;
	}

	private static float calculateCombinedRestitution(
			float restitution0, float restitution1) {
		return restitution0 * restitution1;
	}

	private static class CustomMaterialCombinerCallback
			extends ContactAddedCallback {
		public boolean contactAdded(ManifoldPoint cp,
				CollisionObject colObj0, int partId0,
				int index0, CollisionObject colObj1,
				int partId1, int index1) {
			float friction0 = colObj0.getFriction();
			float friction1 = colObj1.getFriction();
			float restitution0 = colObj0.getRestitution();
			float restitution1 = colObj1.getRestitution();

			if ((colObj0.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
				friction0 = 1f;
				restitution0 = 0f;
			}
			if ((colObj1.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
				if ((index1 & 1) != 0) {
					friction1 = 1f;
				} else {
					friction1 = 0f;
				}
				restitution1 = 0f;
			}

			cp.combinedFriction = calculateCombinedFriction(
					friction0, friction1);
			cp.combinedRestitution = calculateCombinedRestitution(
					restitution0, restitution1);

			return true;
		}
	}

	/*
	 * public Vector3f QuaternionToEuclidean(Quat4f rotation) { Vector3f
	 * rotationaxes = new Vector3f(); rotationaxes.x = (float)Math.asin(2 *
	 * (rotation.w * rotation.y - rotation.z * rotation.x)); float test =
	 * rotation.x * rotation.y + rotation.z * rotation.w; if (test == .5f) {
	 * rotationaxes.y = 2 * (float)Math.atan2(rotation.x, rotation.w);
	 * rotationaxes.z = 0; } else if (test == -.5f) { rotationaxes.y = -2 *
	 * (float)Math.atan2(rotation.x, rotation.w); rotationaxes.z = 0; } else {
	 * rotationaxes.y = (float)Math.atan(2 * (rotation.w * rotation.z +
	 * rotation.y * rotation.y) / (1 - 2 * (rotation.y * rotation.y + rotation.z
	 * * rotation.z))); rotationaxes.z = (float)Math.atan(2 * (rotation.w *
	 * rotation.x + rotation.y * rotation.z) / (1 - 2 * (rotation.x * rotation.x
	 * + rotation.y * rotation.y))); } return rotationaxes; }
	 */

	public UAVASMKQuadrotor getMKQ(int index) {
		return uavs.get(index);
	}

	public static void main(String[] args)
			throws LWJGLException {
		TSimUAVAS sim = new TSimUAVAS(LWJGL.getGL());
		sim.initPhysics();

		sim.spawnMKQuadrotor(new Vector3f(10, 10, 25));
		sim.spawnMKQuadrotor(new Vector3f(10, 14, 25));
		sim.spawnMKQuadrotor(new Vector3f(14, 10, 25));
		sim.spawnMKQuadrotor(new Vector3f(14, 14, 25));
		sim.spawnUFO(new Vector3f(-20f, 10f, 3f));

		LWJGL.main(null, 800, 600, "UAVAS Team Sim",
				instance);

	}

}
