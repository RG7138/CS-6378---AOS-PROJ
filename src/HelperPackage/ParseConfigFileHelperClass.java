package HelperPackage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParseConfigFileHelperClass {
	
	public static ConfigStructure mapFile;
	
	public static void print() throws IOException{
		ConfigStructure m = mapFile;
		
		System.out.println("\nAvailable Nodes and port Details:->");
		System.out.println("Host,Node,Port: ->");
		for(NodeStructure n : m.nodes) {
			System.out.println(n.host + " " + n.nodeId + " " + n.portno);
		}
		
		System.out.println("\n-------------------------------------------------------------------------------------------");
		
		System.out.println("Node "+m.id+" Details:->");
		
		System.out.println("\nConfiguration Details:->");
		System.out.println("Number of Nodes:"+m.numOfNodes);
		System.out.println("Min Active Msg:"+m.minPerActive);
		System.out.println("Max Active Msg:"+m.maxPerActive);
		System.out.println("Min Msg Send Delay:"+m.minSendDelay);
		System.out.println("Max Msg Send Delay"+m.snapshotDelay);
		System.out.println(m.maxNumber);
		
		System.out.println("\nNode's Immediate Neighbours:->");
		System.out.println(m.neighbors);
		System.out.println("\nNode's Parent:"+SpanningTreeHelperClass.returnParent(m.id));
		
		System.out.println("\nAdjancy Matrix for spanning Tree->");
		for(int i=0;i<m.numOfNodes;i++){
			for(int j=0;j<m.numOfNodes;j++){
				System.out.print(m.LinkMatrix[i][j]+"  ");
			}
			System.out.println();
		}
	}
	
	public static ConfigStructure ParseConfigFile(String name) throws IOException{
		
		int Counter = 0;
		int nextConfigInfoPart = 0;
		
		// To Keep track of current node
		int NodeId = 0;
		
		mapFile = new ConfigStructure();
		
		String filepath = System.getProperty("user.dir") + "/" + name;
		
		String line = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			
			while((line = br.readLine()) != null) {
				
				// Ignore comments that is anything after #
				if(line.length() == 0 || line.startsWith("#"))
					continue;
				
				String[] ParsedValues;
				
				if(line.contains("#")){
					//spliting line based on # (sharp symbol)
					String[] CommentLine = line.split("#.*$"); 
					
					//Ignore text after # symbol and splitting on space deliminator
					ParsedValues = CommentLine[0].split("\\s+");
				}
				else {
					ParsedValues = line.split("\\s+");
				}
				
				//Initiate configurations
				if((nextConfigInfoPart == 0) && (ParsedValues.length == 6)){
					mapFile.numOfNodes = Integer.parseInt(ParsedValues[0]);
					mapFile.minPerActive = Integer.parseInt(ParsedValues[1]);
					mapFile.maxPerActive = Integer.parseInt(ParsedValues[2]);
					mapFile.minSendDelay = Integer.parseInt(ParsedValues[3]);
					mapFile.snapshotDelay = Integer.parseInt(ParsedValues[4]);
					mapFile.maxNumber = Integer.parseInt(ParsedValues[5]);
					mapFile.LinkMatrix = new int[mapFile.numOfNodes][mapFile.numOfNodes];
					nextConfigInfoPart++;
				}
				
				//Connection information part
				else if((nextConfigInfoPart == 1) && (Counter < mapFile.numOfNodes))
				{	
					//"127.0.0.1" , for debugging locally  
					if(ParsedValues[1].contains("utdallas.edu") || ParsedValues[1].contains("127.0.0.1")) {
						mapFile.nodes.add(new NodeStructure(Integer.parseInt(ParsedValues[0]),ParsedValues[1],Integer.parseInt(ParsedValues[2])));
					}
					else {
						mapFile.nodes.add(new NodeStructure(Integer.parseInt(ParsedValues[0]),ParsedValues[1]+".utdallas.edu",Integer.parseInt(ParsedValues[2])));
					}
					Counter++;
					if(Counter == mapFile.numOfNodes){
						nextConfigInfoPart = 2;
					}
				}
				
				//Spanning Tree part
				else if(nextConfigInfoPart == 2) {
					for(String i : ParsedValues){
						if(NodeId != Integer.parseInt(i)) {
							mapFile.LinkMatrix[NodeId][Integer.parseInt(i)] = 1;
							mapFile.LinkMatrix[Integer.parseInt(i)][NodeId] = 1;
						}
					}
					NodeId++;
				}
			}
			br.close();  
		}
		catch(Exception e) {
			System.out.println("Error Occured while parsing: '" + filepath + "'"); 
		}
		return mapFile;
	}
	
}

