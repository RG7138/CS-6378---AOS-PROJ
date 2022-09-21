package HelperPackage;
// Object that will have <Identifier> <Hostname> <Port> read from config file stored
public class NodeStructure {
	public int nodeId;
	String host;
	int port;
	public NodeStructure(int nodeId, String host, int port) {
		super();
		this.nodeId = nodeId;
		this.host = host;
		this.port = port;
	}
}