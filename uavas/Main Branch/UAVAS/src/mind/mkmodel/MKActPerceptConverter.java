package mind.mkmodel;

import mind.engine.GeoPoint;
import mind.engine.IActPerceptConverter;
import mind.engine.IOManager;
import jason.asSyntax.Literal;

public class MKActPerceptConverter implements
		IActPerceptConverter {

	/*
	 * | Start-Byte | Address Byte | ID-Byte | n Data-Bytes coded | CRC-Byte1 |
	 * CRC-Byte2 | Stop-Byte |
	 */
	/*
	 * | '#' | 'a'+ Addr | 'V','D' etc | "modified-base64" | variable | variable
	 * | '\r' |
	 */

	private IOManager iom = new IOManager();
	public int uav_id = 1; // id used to identify each agent when communication
							// is set, port 1 as default.

	@Override
	public boolean move_to(GeoPoint p) {
		MKWayPoint wp = Geo2WP(p);
		String s = "#a2s" + wp
				+ iom.checksum(wp.toString().getBytes())
				+ '\r';
		iom.write(s.getBytes(), uav_id);
		return true;
	}

	@Override
	public boolean add_waypoint(GeoPoint p) {
		MKWayPoint wp = Geo2WP(p);
		String s = "#a2w" + wp
				+ iom.checksum(wp.toString().getBytes())
				+ '\r';
		iom.write(s.getBytes(), uav_id);
		return true;
	}

	@Override
	public void set_home(GeoPoint p) {
	}

	@Override
	public GeoPoint get_home() {
		return new GeoPoint(0, 0, 0);
	}

	@Override
	public boolean request(String agName, Literal l) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inform(String agName, Literal l) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ask(String agName, Literal l) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ack(String agName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String check_system() {
		// TODO Auto-generated method stub
		return "ok";
	}

	@Override
	public float check_battery() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public GeoPoint check_location() {
		// TODO Auto-generated method stub
		return new GeoPoint(0, 0, 0);
	}

	private MKWayPoint Geo2WP(GeoPoint p) {
		return new MKWayPoint();
	}

}
