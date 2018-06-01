package mind.arch;

public class GeoPoint {

	private double latitute;
	private double longitude;
	private double height;
	
	public GeoPoint() {
		latitute = 0;
		longitude = 0;
		height = 0;
	}
	
	public GeoPoint(double latitude, double longitude, double height) {
		this.latitute = latitude;
		this.longitude = longitude;
		this.height = height;
	}

	public double getLatitute() {
		return latitute;
	}

	public void setLatitute(double latitute) {
		this.latitute = latitute;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
}
