package mind.mkmodel;

import mind.engine.GeoPoint;
import mind.engine.IOManager;

public class MKSignalDecoder {

	public static byte[] longToByteArray(long data) {
		return new byte[] { (byte) ((data >> 56) & 0xff),
				(byte) ((data >> 48) & 0xff),
				(byte) ((data >> 40) & 0xff),
				(byte) ((data >> 32) & 0xff),
				(byte) ((data >> 24) & 0xff),
				(byte) ((data >> 16) & 0xff),
				(byte) ((data >> 8) & 0xff),
				(byte) ((data >> 0) & 0xff), };
	}

	// return: 'k' MK3MAG-Addr Nick Roll Attitude ...
	// param: 'K' FC-Addr s16 Compass Value
	public static Object CompassHeading(byte Value) {
		return 0;
	};

	// return: 'T' FC-Addr -
	// param:'t' FC-Addr u8[16] values for the engines
	/* public static void EngineTest(byte[] values) {} */

	// return: 'Q' FC-Addr u8 Settings Index, u8 Settings Version, Settings
	// Struct
	// param: 'q' FC-Addr
	// u8 Settings Index ( 1..5 READ or 0xff for actual setting)
	// u8 Settings Index (11..15 RESET setting to default (channel mapping will
	// not be changed))
	// u8 Settings Index (21..25 RESET setting to default (complete reset
	// including channel settings))
	public static byte SettingsRequest(byte index) {
		return 0;
	}

	// return: 'S' FC-Addr u8 Settings Index (1 ..5, 0=Error)
	// param: 's' FC-Addr u8 Settings Index, u8 Settings Version, Settings
	// Struct
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

	// return: - - -
	// param: 'y' FC-Addr s8 Poti[12]
	public static void SerialPoti(byte[] Poti) {
	}

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
	public static void SendTargetPosition(Object WayPoint) {
		GeoPoint p = (GeoPoint) WayPoint;
		byte[] arg1 = new byte[64];
		byte[] arg2 = new byte[64];
		byte[] arg3 = new byte[64];
		byte[] RxTxData = new byte[256];
		arg1 = longToByteArray(Float.floatToIntBits(p
				.getLatitute()));
		arg2 = longToByteArray(Float.floatToIntBits(p
				.getLongitude()));
		arg3 = longToByteArray(Float.floatToIntBits(p
				.getHeight()));
		for (int i = 0; i < arg1.length; i++)
			RxTxData[i] = arg1[i];
		for (int i = 0; i < arg2.length; i++)
			RxTxData[i + 64] = arg2[i];
		for (int i = 0; i < arg3.length; i++)
			RxTxData[i + 128] = arg3[i];
		IOManager iom = new IOManager();
		iom.write(RxTxData, 1);
	}

	// return: none 'W' NC-Addr u8 Number of WPs
	// param: 'w' NC-Addr WayPointStruct (sending an invalid position will clear
	// the WPList)
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
	// param: 'u' NC-Addr 1 byte param for uart selector (0=FC, 1=MK3MAG,
	// 2=MKGPS), can be switched back to NC debug by sending the magic packet
	// "0x1B,0x1B,0x55,0xAA,0x00"
	public static void RedirectUART(byte selector) {
	}

	// return: 'J' NC_Addr u8 prameterId, s16 value
	// param: 'j' !NC-Addr u8 get(0)/set(1),u8 prameterId, s16 value (only when
	// set)
	public static short NCParameter(short value) {
		return 0;
	}

}
