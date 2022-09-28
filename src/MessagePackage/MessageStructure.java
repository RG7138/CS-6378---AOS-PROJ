package MessagePackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("serial")
public class MessageStructure implements Serializable {

	//MapProtocol m = new MapProtocol();
	//int n = m.numOfNodes;
}

@SuppressWarnings("serial")
// Sends string message and vector timestamp
class AppMessage extends MessageStructure implements Serializable{
	public String msg = "Test Message";
	int nodeId;
}

