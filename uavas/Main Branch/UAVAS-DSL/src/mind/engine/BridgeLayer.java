package mind.engine;

import jason.asSyntax.Literal;
import mind.arch.GeoPoint;
//import mind.bridge.MikroKopterNaviCtrl;

public class BridgeLayer implements IBridgeLayer {

	/* | Start-Byte | Address Byte |   ID-Byte   | n Data-Bytes coded | CRC-Byte1 | CRC-Byte2 | Stop-Byte | */
	/* |     '#'    |   'a'+ Addr  | 'V','D' etc |  "modified-base64" |  variable | variable  |    '\r'   | */
	
	@Override
	public boolean move_to(GeoPoint p) {
		double lat = p.getLatitute();
		double lon = p.getLongitude();
		double hei = p.getHeight();
		// param: 's' NC-Addr WayPointStruct
		char message = '#';
		byte frame[];
		//MikroKopterNaviCtrl.SendTargetPosition(p);
		return false;
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
		return null;
	}

	@Override
	public double check_battery() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GeoPoint check_location() {
		// TODO Auto-generated method stub
		GeoPoint p = new GeoPoint();
		return p;
	}
	
}
