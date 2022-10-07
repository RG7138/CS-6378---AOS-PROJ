package HelperPackage;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import MessagePackage.MessageStructure.ApplicatonMessage;

@SuppressWarnings("serial")
public class ConfigStructure implements Serializable  {
	
	//Variables required for MAP Protocol
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
	
	public HashMap<Integer,ObjectOutputStream> oStream;
	
	public HashMap<Integer,Boolean> nodesLocalState;
	
	public HashMap<Integer,Boolean> StateMsgList;
	
	public boolean saveLocalState = false;
	
	public HashMap<Integer, Boolean> nodemsgStatus;
	
	//To check for in-transit messages
	public HashMap<Integer,ArrayList<ApplicatonMessage>> AppMsgList;
	
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
		oStream = new HashMap<Integer,ObjectOutputStream>();	
		saveLocalState = false;
		Snapshots = new ArrayList<int[]>();
	}
	
	public void setValues(ConfigStructure conf) {
		
		conf.MarkerMsgList = new HashMap<Integer,Boolean>();
		
		conf.StateMsgList = new HashMap<Integer,Boolean>();
		
		for (int i: conf.neighbors) {
			conf.MarkerMsgList.put(i,false);
		}
		
		nodesLocalState =  new HashMap<Integer,Boolean>();
		
		conf.AppMsgList = new HashMap<Integer,ArrayList<ApplicatonMessage>>();
		
		for(var i:conf.channels.keySet()) {
			
			ArrayList<ApplicatonMessage> arrList = new ArrayList<ApplicatonMessage>();
			conf.AppMsgList.put(i, arrList);
			
		}
		
		conf.nodemsgStatus = new HashMap<Integer,Boolean>();
		
	} 
	
	
}	

