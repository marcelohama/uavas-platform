package mind.engine;

public interface IIOManager {

	public boolean write(String data, int port);

	public boolean write(byte[] data, int port);

	public String read(int port);

}
