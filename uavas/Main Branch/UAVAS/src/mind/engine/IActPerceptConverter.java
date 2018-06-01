package mind.engine;

import jason.asSyntax.Literal;

public interface IActPerceptConverter {
	// logistic actions
	public boolean move_to(GeoPoint p);

	public boolean add_waypoint(GeoPoint p);

	public void set_home(GeoPoint p);

	public GeoPoint get_home();

	// communication actions
	public boolean request(String agName, Literal l);

	public boolean inform(String agName, Literal l);

	public boolean ask(String agName, Literal l);

	public boolean ack(String agName);

	// perceive actions
	public String check_system();

	public float check_battery();

	public GeoPoint check_location();
}
