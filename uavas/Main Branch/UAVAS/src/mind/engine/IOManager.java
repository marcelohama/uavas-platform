package mind.engine;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

public class IOManager implements IIOManager {

	private Enumeration<?> portList;
	private CommPortIdentifier portId;
	private SerialPort serialPort = null;
	private OutputStream outputStream = null;

	public byte[] checksum(byte[] dataBuffer) {
		int tmpCRC = 0;
		for (int i = 0; i < dataBuffer.length; i++) {
			tmpCRC += dataBuffer[i];
		}
		tmpCRC %= 4096;
		byte[] crc = new byte[2];
		crc[0] = (byte) ('=' + tmpCRC / 64);
		crc[1] = (byte) ('=' + tmpCRC % 64);
		return crc;
	}

	@Override
	public boolean write(String data, int port) {
		return write(data.getBytes(), port);
	}

	@Override
	public boolean write(byte[] data, int port) {
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList
					.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM" + port)) {
					try {
						serialPort = (SerialPort) portId
								.open("IOManager" + port,
										1000);
						outputStream = serialPort
								.getOutputStream();
						serialPort.setSerialPortParams(
								9600,
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
						outputStream.write(data);
					} catch (PortInUseException e) {
						System.out.println("port in use");
						return false;
					} catch (UnsupportedCommOperationException e) {
						System.out.println("unsupported");
						return false;
					} catch (IOException e) {
						System.out.println("io error");
						return false;
					}
					serialPort.close();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String read(int port) {
		// TODO Auto-generated method stub
		return null;
	}

}
