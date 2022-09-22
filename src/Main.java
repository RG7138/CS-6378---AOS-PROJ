import java.io.IOException;

import HelperPackage.ClientConnectionHelperClass;
import HelperPackage.ConfigStructure;
import HelperPackage.ParseConfigFileHelperClass;
import HelperPackage.ServerConnectionHelperClass;
import HelperPackage.SpanningTreeHelperClass;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//final int NODE_ZERO = 0;
		
		//Parse through config.txt file
		ConfigStructure mapObject = ParseConfigFileHelperClass.readtxt(args[1]);
		
		// Get the node number of the current Node
		mapObject.id = Integer.parseInt(args[0]);
		
		int current = mapObject.id;
		
		//Get the configuration file name from command line
		mapObject.title = args[1];
		
		ConfigStructure.outFile = mapObject.title.substring(0, mapObject.title.lastIndexOf('.'));
		
		//Build converge cast spanning tree
		SpanningTreeHelperClass.TreeMtx(mapObject.adjacent);
		
		// Transfer the collection of nodes from ArrayList to hash map nodes
		for(int i=0;i<mapObject.nodes.size();i++){
			mapObject.information.put(mapObject.nodes.get(i).nodeId, mapObject.nodes.get(i));
		}
		
		//Create a server socket 
		ServerConnectionHelperClass server = new ServerConnectionHelperClass(mapObject);
		
		//Create channels and keep it till the end
		new ClientConnectionHelperClass(mapObject, current);

		mapObject.vector = new int[mapObject.numOfNodes];

		//Initialize all data structures
		mapObject.initialize(mapObject);
		
		try {
			ParseConfigFileHelperClass.print();
		}
		catch(Exception e){
			System.out.println("Error occured while parsing ConfigFile:->"+e);
		}

		//Initially node 0 is active therefore if this node is 0 then it should be active
//		if(current == NODE_ZERO){
//			mapObject.active = true;		
//			//Call Chandy Lamport protocol if it is node 0
//			new CL_Protocol_Thread(mapObject).start();		
//			new SendMessageThread(mapObject).start();
//		}
		
		server.listenforinput(); //Listen for client connections
		
	}
}
