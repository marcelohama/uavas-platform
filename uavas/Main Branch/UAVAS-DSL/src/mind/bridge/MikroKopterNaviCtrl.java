package mind.bridge;

public class MikroKopterNaviCtrl {

	// return: 'Z' NC-Addr u16 EchoPattern
	// param: 'z' NC-Addr u16 EchoPattern
	public static short SerialLinkTest(short EchoPattern) {
		return 0;
	}

	// return: 'E' NC-Addr char[] Error Message String
	// param: 'e' NC-Addr none
	public static byte[] ErrorTextRequest() {
		return null;
	}
	
	// return: none
	// param: 's' NC-Addr WayPointStruct
	public static void SendTargetPosition(Object WayPoint) {}
	
	// return: none 'W' NC-Addr u8 Number of WPs
	// param: 'w' NC-Addr WayPointStruct (sending an invalid position will clear the WPList)
	public static byte SendWaypoint(Object WayPoint) {
		return 0;
	}
	
	// return: 'X' NC-Addr u8 Number of WPs, u8 WP-Index, WayPointStruct
	// param: 'x' NC-Addr u8 WP-Index
	public static Object RequestWaypoint(byte WPIndex) {
		return 0;
	}
	
	// return 'O' NC-Addr NaviDataStruct
	// param: 'o' NC-Addr 1 byte sending interval ( in 10ms steps )
	public static Object RequestOSDData(byte interval) {
		return 0;
	}
	
	// return none
	// param: 'u' NC-Addr 1 byte param for uart selector (0=FC, 1=MK3MAG, 2=MKGPS), can be
	// switched back to NC debug by sending the magic packet "0x1B,0x1B,0x55,0xAA,0x00"
	public static void RedirectUART(byte selector) {}
	
	// return: 'C' NC-Addr struct Data3D
	// param: 'c' AnyAddr u8 Interval
	public static Object Set3DDataInterval(byte Interval) {
		return 0;
	}
	
	// return: 'J' NC_Addr u8 prameterId, s16 value
	// param: 'j' !NC-Addr u8 get(0)/set(1),u8 prameterId, s16 value (only when set)
	public static short NCParameter(short value) {
		return 0;
	}
	
}
