package MessagePackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.util.HashMap;

import MessagePackage.MessageStructure.ApplicationMessage;
import MessagePackage.MessageStructure.MarkerMsg;
import MessagePackage.MessageStructure.MessageStructure;
import MessagePackage.MessageStructure.StateMsg;
import MessagePackage.MessageStructure.TerminateMsg;
import HelperPackage.ConfigStructure;

public class ReceiveMessageClass extends Thread {
	Socket socket;
	ConfigStructure nodeObj;

	public ReceiveMessageClass(Socket ClientSocket,ConfigStructure nodeObj) {
		this.socket = ClientSocket;
		this.nodeObj = nodeObj;
	}
	
	public void writeMsgtofile(MessageStructure msg) {
		
		if(msg instanceof ApplicationMessage) {
		
			ApplicationMessage tmp = (ApplicationMessage)msg;
			String OutFileName = ConfigStructure.outFile + "-" + nodeObj.id + ".out";
			
			try {
				File file = new File(OutFileName);
				FileWriter fileWriter;
				if(file.exists()){
					fileWriter = new FileWriter(file,true);
				}
				else
				{
					fileWriter = new FileWriter(file);
				}
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				
				bufferedWriter.write("\n"+"'"+ tmp.msg +"'"+" received from Node:"+ tmp.nodeId+"\n\n");
				
				bufferedWriter.close();
				
			}
			catch(Exception e) {
				System.out.println("Error: Unable to write to file '" + OutFileName + "'");
				e.printStackTrace();
			}
		}
		else if(msg instanceof TerminateMsg) {
			
			String OutFileName = ConfigStructure.outFile + "-" + nodeObj.id + ".out";
			
			try {
				File file = new File(OutFileName);
				FileWriter fileWriter;
				if(file.exists()){
					fileWriter = new FileWriter(file,true);
				}
				else
				{
					fileWriter = new FileWriter(file);
				}
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				
				int count =0;
				for(int[] i:nodeObj.Snapshots) {
					count++;
					bufferedWriter.write("\nSnap Number - " + count +"\n");
					
					for(int j: i) {
						bufferedWriter.write(j+" ");
					}
					
				}
				
				bufferedWriter.write("\n\n\nSummary - \n\n");
				
				bufferedWriter.write("\nLocal Vector Clock-\n");
				for(int i:nodeObj.vectorClock) {
					bufferedWriter.write(i+" ");
				}
				
				bufferedWriter.write("\nTotal Application Messages Sent - \n" + nodeObj.msgSentCount + "\nTotal Application Messages Received - \n" + nodeObj.msgReceiveCount);
				bufferedWriter.close();
				
				System.out.println("\nLogs written to file - " + OutFileName);
				
			}
			catch(Exception e) {
				System.out.println("Error: Unable to write to file '" + OutFileName + "'");
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void MessageListener(ObjectInputStream ois) {
		try {
			MessageStructure msg;
			msg = (MessageStructure) ois.readObject();
			
			synchronized(nodeObj){
				
				if(msg instanceof ApplicationMessage){
					
					//Updating vector clock on receiving an Application message
					for(int i=0;i<nodeObj.numOfNodes;i++){
						nodeObj.vectorClock[i] = Math.max(nodeObj.vectorClock[i], ((ApplicationMessage) msg).vectorClock[i]);
					}
					nodeObj.vectorClock[nodeObj.id]++;
					
				}
				
				
   				//A passive Node on receiving an application message , will becomes active only if it has sent fewer than maxNumber messages ; 2nd condition of the MAP protocol
				if((msg instanceof ApplicationMessage) && 
						(!nodeObj.active) && 
						(nodeObj.msgSentCount < nodeObj.maxNumber)&& (!nodeObj.saveLocalState) )  //&& (!nodeObj.saveLocalState)
				{
			
					ApplicationMessage tmp = (ApplicationMessage) msg;
					nodeObj.active = true;
					nodeObj.msgReceiveCount++;
					writeMsgtofile(tmp);
					new SendMessageClass(nodeObj).start();
				}
				else if((msg instanceof ApplicationMessage) && (!nodeObj.active) && (nodeObj.saveLocalState)) {
					
					nodeObj.msgReceiveCount++;
					int channelNo = ((ApplicationMessage)msg).nodeId;
					SnapshotProtocolClass.saveAppMsg(channelNo,(ApplicationMessage)msg,nodeObj);
					
				}
				
				else if(msg instanceof StateMsg) {
					
					
					System.out.println("\nState Msg received\n");
					if(nodeObj.id == 0) {
						
						//Keeping track of marker messages received from each node in the system
						nodeObj.StateMsgList.put(((StateMsg)msg).nodeId, true);
						
						//Whether the node is active or passive when the snapshot was taken
						nodeObj.nodesLocalState.put(((StateMsg)msg).nodeId, ((StateMsg)msg).currnodestate);
						
						//Each nodes in-transit message status
						nodeObj.nodemsgStatus.put(((StateMsg)msg).nodeId, ((StateMsg)msg).nodeStatus);
						
						//nodeObj.AppMsgList.get(((StateMsg)msg).nodeId).add(msg);
						
						if (nodeObj.StateMsgList.size() == nodeObj.numOfNodes-1) {
							
							System.out.println("\nAll state messages received checking for nodes active status\n");
							
							boolean restart = false;
							
							//Detect Termination
							restart = SnapshotProtocolClass.Detecttermination(nodeObj);
							
							if(restart) {
								nodeObj.setValues(nodeObj);
								new SnapshotHandlerClass(nodeObj).start();
							}
							else {
								System.out.println("\nAll nodes are passive, we can terminate!!!\n");
								//System.exit(3);
								writeMsgtofile(new TerminateMsg());
								SnapshotProtocolClass.sendTerminateMsg(nodeObj);
							}
							
						}
						
					}
					else {
						SnapshotProtocolClass.sendToSource(nodeObj,(StateMsg)msg);
					}
					
				}
				else if(msg instanceof MarkerMsg) {
					int channelNo = ((MarkerMsg)msg).nodeId;
					System.out.println("\nMarker Msg Received\n");
					SnapshotProtocolClass.sendMarkerMsg(nodeObj,channelNo);
				}
				else if(msg instanceof TerminateMsg) {
					writeMsgtofile(new TerminateMsg());
					SnapshotProtocolClass.sendTerminateMsg(nodeObj);
				}
				else {
					nodeObj.msgReceiveCount++;
				}
				
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true){
			MessageListener(ois);
		}
	}
}
