import java.io.IOException;

import HelperPackage.ClientConnectionHelperClass;
import HelperPackage.ConfigStructure;
import HelperPackage.ParseConfigFileHelperClass;
import HelperPackage.ServerConnectionHelperClass;
import HelperPackage.SpanningTreeHelperClass;
import MessagePackage.SendMessageClass;
import MessagePackage.SnapshotHandlerClass;
import MessagePackage.SnapshotProtocolClass;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Parse through config.txt file
		ConfigStructure mapObject = ParseConfigFileHelperClass.ParseConfigFile(args[1]);
		
		// Get the node number of the current Node
		mapObject.id = Integer.parseInt(args[0]);
		
		int curNode = mapObject.id;
		
		//Get the configuration file name from command line
		mapObject.configFile = args[1];
		
		ConfigStructure.outFile = mapObject.configFile.substring(0,mapObject.configFile.lastIndexOf('.'));
		
		//Build converge cast spanning tree
		SpanningTreeHelperClass.buildSpanningTree(mapObject.LinkMatrix);
		
		// Transfer the collection of nodes from ArrayList to hash map nodes
		for(int i=0;i<mapObject.nodes.size();i++){
			mapObject.information.put(mapObject.nodes.get(i).nodeId, mapObject.nodes.get(i));
		}
		
		//Create a server socket 
		ServerConnectionHelperClass server = new ServerConnectionHelperClass(mapObject);
		
		//Create channels and keep it till the end
		new ClientConnectionHelperClass(mapObject, curNode);
		
		try {
			ParseConfigFileHelperClass.print();
		}
		catch(Exception e){
			System.out.println("Error occured while parsing ConfigFile:->"+e);
		}
		
		mapObject.vectorClock = new int[mapObject.numOfNodes];
		mapObject.setValues(mapObject);
		
		
		//Initially node 0 is active therefore if this node is 0 then it should be active
		if(curNode == 0){
			mapObject.active = true;		
			
			//new SnapshotProtocolClass().iniateSnapshot(mapObject);
			
			new SendMessageClass(mapObject).start();
			new SnapshotHandlerClass(mapObject).start();
			
		}
		
		server.AcceptClientConnections(); //Listen for client connections
		
	}
}
