package MessagePackage.MessageStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// Converting String objects to a Stream
@SuppressWarnings("serial")
public class MessageStructure implements Serializable {
	
	public MarkerMsg createMarkerMsg() {
		
		return new MarkerMsg();
	}
	
}
//
//@SuppressWarnings("serial")
//
// //Sends message and the timestamp
//class ApplicatonMessage extends MessageStructure implements Serializable{
//	public String msg = "Test Message";
//	int nodeId;
//	int[] vectorClock;
//}
//
//@SuppressWarnings("serial")
//class MarkerMsg extends MessageStructure implements Serializable{
//	
//	String msg = "MarkerMsg";
//	int nodeId;
//	
//}
//
//class StateMsg extends MessageStructure implements Serializable{
//	
//	String msg = "State Message";
//	int nodeId;
//	boolean currnodestate;
//	HashMap<Integer,ArrayList<MessageStructure>> AppMsgLst;
//	int[] vectorClock;
//	
//}