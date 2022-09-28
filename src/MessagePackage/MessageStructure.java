package MessagePackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("serial")
public class MessageStructure implements Serializable {
}

@SuppressWarnings("serial")
// Sends string message and vector timestamp
class ApplicatonMessage extends MessageStructure implements Serializable{
	public String msg = "Test Message";
	int nodeId;
}

