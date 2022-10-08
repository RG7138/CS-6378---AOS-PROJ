package MessagePackage.MessageStructure;

import java.io.Serializable;


@SuppressWarnings("serial")
public class ApplicationMessage extends MessageStructure implements Serializable{
	public String msg = "Test Message";
	public int nodeId;
	public int[] vectorClock;
}