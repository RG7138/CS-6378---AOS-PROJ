import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
class StateMsg implements Serializable{
	
	String msg = "State Message";
	int nodeId;
	boolean currnodestate;
	int[] vectorClock;
	
}
