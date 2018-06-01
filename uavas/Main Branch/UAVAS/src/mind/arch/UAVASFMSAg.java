package mind.arch;

import java.util.List;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.infra.centralised.RunCentralisedMAS;

public class UAVASFMSAg extends AgArch {

	public static void main(String[] a) {
		RunCentralisedMAS.main(new String[] { "fmssim.mas2j" });
	}

	public UAVASFMSAg() {
	}

	@Override
	public void act(ActionExec action, List<ActionExec> feedback) {
		super.act(action, feedback);
	}

}
