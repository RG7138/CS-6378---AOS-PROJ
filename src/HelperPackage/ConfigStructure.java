package HelperPackage;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import MessagePackage.MessageStructure.ApplicationMessage;

@SuppressWarnings("serial")
public class ConfigStructure implements Serializable  {
	
	public int id;
	public int[][] LinkMatrix;
	public ArrayList<Integer> neighbors;
	public boolean active;
	public int msgSentCount;
	public int msgReceiveCount;
	
	public int numOfNodes;
	public int minPerActive;
	public int maxPerActive;
	public int minSendDelay;
	public int snapshotDelay;
	public int maxNumber;
	
	public int[] vectorClock;
	public String configFile;
	public static String outFile;
	
	public ArrayList<int[]> Snapshots;
	
	//Mapping between process number as keys and NodeStructure as value
	public HashMap<Integer,NodeStructure> information;
	
	public int Snapcount = 0;
	
	//ArrayList which holds the total processes(nodes) 
	public ArrayList<NodeStructure> nodes;
	
	public HashMap<Integer,Boolean> MarkerMsgList;
	
	// Mapping between each process as a server and its client connections
	HashMap<Integer,Socket> channels;
	
	public boolean MarkerSent = false;
	
	//To check if the client nodes are active or passive
	public HashMap<Integer,Boolean> nodesLocalState;
	
	//To keep track of state messages received by the node
	public HashMap<Integer,Boolean> StateMsgList;
	
	//To save application messages and vector clock for snapshot
	public boolean saveLocalState = false;
	
	//To keep a track on in-transit messages
	public HashMap<Integer, Boolean> nodemsgStatus;
	
	//Output channel for each client connection
	public HashMap<Integer,ObjectOutputStream> outStream;
	
	//To check for in-transit messages
	public HashMap<Integer,ArrayList<ApplicationMessage>> AppMsgList;
	
	//Constructor to initialize all variables
	public ConfigStructure() {
		
		Snapcount = 0;
		msgSentCount = 0;
		msgReceiveCount = 0;
		active=false;
		neighbors = new ArrayList<>();
		nodes = new ArrayList<NodeStructure>();
		information = new HashMap<Integer,NodeStructure>();
		channels = new HashMap<Integer,Socket>();
		outStream = new HashMap<Integer,ObjectOutputStream>();	
		saveLocalState = false;
		Snapshots = new ArrayList<int[]>();
	}
	
	
	//To reset the config values
	public void setValues(ConfigStructure conf) {
		
		conf.MarkerMsgList = new HashMap<Integer,Boolean>();
		
		conf.StateMsgList = new HashMap<Integer,Boolean>();
		
		for (int i: conf.neighbors) {
			conf.MarkerMsgList.put(i,false);
		}
		
		nodesLocalState =  new HashMap<Integer,Boolean>();
		
		conf.AppMsgList = new HashMap<Integer,ArrayList<ApplicationMessage>>();
		
		for(int i:conf.channels.keySet()) {
			
			ArrayList<ApplicationMessage> arrList = new ArrayList<ApplicationMessage>();
			conf.AppMsgList.put(i, arrList);
			
		}
		
		conf.nodemsgStatus = new HashMap<Integer,Boolean>();
		
	} 
	
	
}	

