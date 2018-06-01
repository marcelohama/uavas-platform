package mind.bridge;

public class MikroKopterFlightCtrl {

	// return: 'k' MK3MAG-Addr Nick Roll Attitude ...
	// param: 'K' FC-Addr s16 Compass Value
	public static Object CompassHeading(byte Value) {
		return 0;
	};
	
	// return: 'T' FC-Addr - 
	// param:'t' FC-Addr u8[16] values for the engines
	public static void EngineTest(byte[] values) {}
	
	// return: 'Q' FC-Addr u8 Settings Index, u8 Settings Version, Settings Struct 
	// param: 'q' FC-Addr
	// u8 Settings Index ( 1..5 READ or 0xff for actual setting)
	// u8 Settings Index (11..15 RESET setting to default (channel mapping will not be changed))
	// u8 Settings Index (21..25 RESET setting to default (complete reset including channel settings))
	public static byte SettingsRequest(byte index) {
		return 0;
	}
	
	// return: 'S' FC-Addr u8 Settings Index (1 ..5, 0=Error)
	// param: 's' FC-Addr u8 Settings Index, u8 Settings Version, Settings Struct
	public static byte WriteSettings(byte index) {
		return 0;
	}
	
	// return: 'P' FC-Addr s16 PPM-Array[11]
	// param: 'p' FC-Addr none
	public static short[] ReadPPMChannels() {
		return null;
	}
	
	// return: 'C' FC-Addr struct Data3D
	// param: 'c' AnyAddr u8 Interval
	public static Object Set3DDataInterval(byte Interval) {
		return 0;
	}
	
	// return: 'N' FC-Addr u8 MixerRevision, u8 Name[12], u8 MixerTable[16][4]
	// param: 'n' FC-Addr none
	public static byte[] MixerRequest() {
		return null;
	}
	
	// return: 'M' FC-Addr u8 ack (1 = okay, 0 = error)
	// param: 'm' FC-Addr u8 MixerRevision, u8 Name[12], u8 MixerTable[16][4]
	public static byte MixerWrite(byte[] param) {
		return 0;
	}
	
	// return: 'F' FC-Addr u8 Number
	// param: 'f' FC-Addr u8 Number of new Setting
	public static byte ChangeSetting(byte setting) {
		return 0;
	}
	
	// return:  - - - 
	// param: 'y' FC-Addr s8 Poti[12]
	public static void SerialPoti(byte[] Poti) {}
	
	// return: 'U' FC-Addr u8 Status1, u8 Status2, u8 BL_Addr, BLConfig Struct
	// param: 'u' FC-Addr u8 BL_Addr
	public static Object BLParameterRequest(byte BLAddr) {
		return 0;
	}
	
	// return: 'W' FC-Addr u8 Status1, u8 Status2
	// param: 'w' FC-Addr u8 BL_Addr, BLConfig Struct
	public static byte BLParameterWrite(Object config) {
		return 0;
	}

}
