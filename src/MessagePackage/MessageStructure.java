package MessagePackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// Converting String objects to a Stream
@SuppressWarnings("serial")
public class MessageStructure implements Serializable {
}

@SuppressWarnings("serial")

// Sends message and the timestamp
class ApplicatonMessage extends MessageStructure implements Serializable{
	public String msg = "Test Message";
	int nodeId;
}
