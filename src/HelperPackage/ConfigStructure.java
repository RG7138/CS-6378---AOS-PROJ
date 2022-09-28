package HelperPackage;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("serial")
public class ConfigStructure implements Serializable  {
	
	//Variables required for MAP Protocol
	public int id;
	public int[][] LinkMatrix;
	public ArrayList<Integer> neighbors;
	public boolean active;
	public int msgSentCount;
	
	public int numOfNodes;
	public int minPerActive;
	public int maxPerActive;
	public int minSendDelay;
	public int snapshotDelay;
	public int maxNumber;
	
	public String configFile;
	public static String outFile;
	
	//Mapping between process number as keys and NodeStructure as value
	public HashMap<Integer,NodeStructure> information;
	
	
	//ArrayList which holds the total processes(nodes) 
	public ArrayList<NodeStructure> nodes;
	
	
	// Mapping between each process as a server and its client connections
	HashMap<Integer,Socket> channels;
	
	
	public HashMap<Integer,ObjectOutputStream> oStream;
	
	//Constructor to initialize all variables
	public ConfigStructure() {
		msgSentCount = 0;
		active=false;
		neighbors = new ArrayList<>();
		nodes = new ArrayList<NodeStructure>();
		information = new HashMap<Integer,NodeStructure>();
		channels = new HashMap<Integer,Socket>();
		oStream = new HashMap<Integer,ObjectOutputStream>();
	}
	
	
}	

