package mind.engine;

import jason.asSyntax.Literal;
import mind.arch.GeoPoint;

public interface IBridgeLayer {
	// logistic actions
	public boolean move_to(GeoPoint p);
	// communication actions
	public boolean request(String agName, Literal l);
	public boolean inform(String agName, Literal l);
	public boolean ask(String agName, Literal l);
	public boolean ack(String agName);
	// perceive actions
	public String check_system();
	public double check_battery();
	public GeoPoint check_location();
}
