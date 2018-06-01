package simulators.uavastsim;

import mind.engine.GeoPoint;
import mind.engine.ProtocolConnection;

import jason.asSyntax.*;

public class TSimEnvironment extends ProtocolConnection {

	protected int id = 0;
	public TSimUAVAS sim = null;

	public void setConnectionId(int id) {
		this.id = id;
	}

	public int getConnectionId() {
		return id;
	}

	@Override
	public void init(String[] args) {
		super.init(args);
	}

	@Override
	public boolean executeAction(String agName,
			Structure action) {
		super.executeAction(agName, action);

		GeoPoint p = null;
		UAVASActions act = null;

		if (action.getFunctor().toString()
				.equals("registerId")) {
			setConnectionId(Integer.parseInt(action
					.getTerm(0).toString()));
			return true;
		}
		try {
			act = UAVASActions.valueOf(action.getFunctor()
					.toString());
		} catch (IllegalArgumentException e) {
			return true;
		}

		switch (act) {
		case uavasInit:
			try {
				p = sim.uavs.get(id - 1).getLocation();
				removePerceptsByUnif(
						agName,
						Literal.parseLiteral("location(_,_,_)"));
				addPercept(
						agName,
						Literal.parseLiteral("location("
								+ p.getLatitute() + ","
								+ p.getLongitude() + ","
								+ p.getHeight() + ")"));
				removePerceptsByUnif(agName,
						Literal.parseLiteral("home(_,_,_)"));
				addPercept(
						agName,
						Literal.parseLiteral("home("
								+ p.getLatitute() + ","
								+ p.getLongitude() + ","
								+ p.getHeight() + ")"));
				sim.uavs.get(id - 1).setHome(
						new GeoPoint(p.getLatitute(), p
								.getLongitude(), p
								.getHeight()));
				bridgeConnection.uav_id = id;
			} catch (Exception e) {
			}
			break;
		case clearPath:
			sim.uavs.get(id - 1).clearPath();
			break;
		case doPatrol:
			sim.uavs.get(id - 1).doPatrol();
			break;
		case checkLocation:
			p = sim.uavs.get(id - 1).getLocation();
			removePerceptsByUnif(agName,
					Literal.parseLiteral("location(_,_,_)"));
			addPercept(
					agName,
					Literal.parseLiteral("location("
							+ p.getLatitute() + ","
							+ p.getLongitude() + ","
							+ p.getHeight() + ")"));
			break;
		case moveTo:
			sim.uavs.get(id - 1).moveTo(
					new GeoPoint(Float.parseFloat(action
							.getTerm(0).toString()), Float
							.parseFloat(action.getTerm(1)
									.toString()), Float
							.parseFloat(action.getTerm(2)
									.toString())));
			break;
		case addWayPoint:
			sim.uavs.get(id - 1).addWayPoint(
					new GeoPoint(Float.parseFloat(action
							.getTerm(0).toString()), Float
							.parseFloat(action.getTerm(1)
									.toString()), Float
							.parseFloat(action.getTerm(2)
									.toString())));
			break;
		case setHome:
			sim.uavs.get(id - 1).setHome(
					new GeoPoint(Float.parseFloat(action
							.getTerm(0).toString()), Float
							.parseFloat(action.getTerm(1)
									.toString()), Float
							.parseFloat(action.getTerm(2)
									.toString())));
			break;
		case goHome:
			sim.uavs.get(id - 1).moveTo(
					sim.uavs.get(id - 1).getHome());
			break;
		case checkBattery:
			float battery = sim.uavs.get(id - 1)
					.getBattery();
			removePerceptsByUnif(agName,
					Literal.parseLiteral("battery(_)"));
			addPercept(
					agName,
					Literal.parseLiteral("battery("
							+ battery + ")"));
			break;
		case request:
			try {
				sim.uavs.get(
						Integer.parseInt(action.getTerm(0)
								.toString()) - 1)
						.messageHandler(
								id,
								action.getTerm(1)
										.toString(), 'R');
			} catch (Exception e) {
			}
			break;
		case inform:
			try {
				sim.uavs.get(
						Integer.parseInt(action.getTerm(0)
								.toString()) - 1)
						.messageHandler(
								id,
								action.getTerm(1)
										.toString(), 'I');
			} catch (Exception e) {
			}
			break;
		case ack:
			try {
				sim.uavs.get(
						Integer.parseInt(action.getTerm(0)
								.toString()) - 1)
						.messageHandler(
								id,
								action.getTerm(1)
										.toString(), 'K');
			} catch (Exception e) {
			}
			break;
		case ask:
			try {
				sim.uavs.get(
						Integer.parseInt(action.getTerm(0)
								.toString()) - 1)
						.messageHandler(
								id,
								action.getTerm(1)
										.toString(), 'A');
			} catch (Exception e) {
			}
			break;
		}
		return true;
	}

}
