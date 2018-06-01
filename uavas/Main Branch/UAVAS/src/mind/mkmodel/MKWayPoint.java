package mind.mkmodel;

public class MKWayPoint {

	// GPS_Pos_t
	int Longitude; // in 1E-7 deg
	int Latitude; // in 1E-7 deg
	int Altitude; // in mm
	byte Status; // validity of data

	short Heading; // orientation, future implementation
	byte ToleranceRadius; // in meters, if the MK is within that range around
							// the target, then the next target is triggered
	byte HoldTime; // in seconds, if the was once in the tolerance area around a
					// WP, this time defines the delay before the next WP is
					// triggered
	byte Event_Flag; // future implementation
	byte Index; // to indentify different waypoints, workaround for bad
				// communications PC <-> NC
	byte Type; // typeof Waypoint
	byte[] reserve = new byte[10]; // reserve

}
