package MessagePackage.MessageStructure;
import java.io.Serializable;

@SuppressWarnings("serial")
public class MarkerMsg extends MessageStructure implements Serializable{
	
	String msg = "MarkerMsg";
	public int nodeId;
	
}