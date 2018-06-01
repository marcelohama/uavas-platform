package simulators.fmssim;

import mind.engine.IActPerceptConverter;

public interface IFMSUAVASBridge extends
		IActPerceptConverter {
	// misc
	public void yaw(int value);

	public void pitch(int value);

	public void roll(int value);

	public void coletive(int value);
}
