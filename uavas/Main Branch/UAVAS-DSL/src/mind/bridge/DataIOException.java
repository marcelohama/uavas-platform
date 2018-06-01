package mind.bridge;

public class DataIOException extends java.lang.Exception {
	
	private static final long serialVersionUID = 1L;

	public static DataIOException BridgeCallFailed() {
        return new DataIOException();
    }
	
}
