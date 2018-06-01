package mind.arch;

import java.util.List;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.infra.centralised.RunCentralisedMAS;

public class UAVASAgArch extends AgArch {
	
	public static void main(String[] a) {
        RunCentralisedMAS.main(new String[]{"default.mas2j"});
    }
	
	public UAVASAgArch() {
	}
	
    @Override
    public void act(ActionExec action, List<ActionExec> feedback) {
    	super.act(action, feedback);
    	//System.out.println(action.getActionTerm());
    }
    
}
