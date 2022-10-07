package MessagePackage.MessageStructure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class StateMsg extends MessageStructure implements Serializable{
	
	String msg = "State Message";
	public int nodeId;
	public boolean currnodestate;
	int[] vectorClock;
	//public HashMap<Integer, ArrayList<ApplicatonMessage>> AppMsgLst;
	public boolean nodeStatus = false;
}
