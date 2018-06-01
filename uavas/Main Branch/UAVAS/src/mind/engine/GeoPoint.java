package mind.engine;

public class GeoPoint {

	private float latitute;
	private float longitude;
	private float height;

	public GeoPoint() {
		latitute = 0;
		longitude = 0;
		height = 0;
	}

	public GeoPoint(float latitude, float longitude,
			float height) {
		this.latitute = latitude;
		this.longitude = longitude;
		this.height = height;
	}

	public float getLatitute() {
		return latitute;
	}

	public void setLatitute(float latitute) {
		this.latitute = latitute;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

}
