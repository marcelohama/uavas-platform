package simulators.fmssim;

import mind.engine.IOManager;
import mind.mkmodel.MKActPerceptConverter;

public class FMSActPerceptConverter extends
		MKActPerceptConverter implements IFMSUAVASBridge {

	protected int system_config[] = new int[8];

	public FMSActPerceptConverter() {
		for (int i = 0; i < 8; i++) {
			system_config[i] = 5;
		}
	}

	@Override
	public void yaw(int value) {
		system_config[0] = value;
		IOManager iom = new IOManager();
		iom.write("ñ=" + system_config[0]
				+ system_config[1] + system_config[2]
				+ system_config[3], 2);
	}

	@Override
	public void pitch(int value) {
		system_config[1] = value;
		IOManager iom = new IOManager();
		iom.write("ñ=" + system_config[0]
				+ system_config[1] + system_config[2]
				+ system_config[3], 2);
	}

	@Override
	public void roll(int value) {
		system_config[2] = value;
		IOManager iom = new IOManager();
		iom.write("ñ=" + system_config[0]
				+ system_config[1] + system_config[2]
				+ system_config[3], 2);
	}

	@Override
	public void coletive(int value) {
		system_config[3] = value;
		IOManager iom = new IOManager();
		iom.write("ñ=" + system_config[0]
				+ system_config[1] + system_config[2]
				+ system_config[3], 2);
	}

}
