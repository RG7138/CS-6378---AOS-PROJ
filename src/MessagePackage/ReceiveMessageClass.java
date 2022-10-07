package MessagePackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.util.HashMap;

import MessagePackage.MessageStructure.ApplicatonMessage;
import MessagePackage.MessageStructure.MarkerMsg;
import MessagePackage.MessageStructure.MessageStructure;
import MessagePackage.MessageStructure.StateMsg;
import MessagePackage.MessageStructure.TerminateMsg;
import HelperPackage.ConfigStructure;

//Read object data sent by neighboring clients
public class ReceiveMessageClass extends Thread {
	Socket socket;
	ConfigStructure mapObject;

	public ReceiveMessageClass(Socket ClientSocket,ConfigStructure mapObject) {
		this.socket = ClientSocket;
		this.mapObject = mapObject;
	}
	
	public void writeMsgtofile(MessageStructure msg) {
		
		if(msg instanceof ApplicatonMessage) {
		
			ApplicatonMessage tmp = (ApplicatonMessage)msg;
			String OutFileName = ConfigStructure.outFile + "-" + mapObject.id + ".out";
			
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
			
			String OutFileName = ConfigStructure.outFile + "-" + mapObject.id + ".out";
			
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
				for(var i:mapObject.Snapshots) {
					count++;
					bufferedWriter.write("\nSnap Number - " + count +"\n");
					
					for(var j: i) {
						bufferedWriter.write(j+" ");
					}
					
				}
				
				bufferedWriter.write("\n\n\nSummary - \n\n");
				
				bufferedWriter.write("\nLocal Vector Clock-\n");
				for(var i:mapObject.vectorClock) {
					bufferedWriter.write(i+" ");
				}
				
				bufferedWriter.write("\nTotal Application Messages Sent - \n" + mapObject.msgSentCount + "\nTotal Application Messages Received - \n" + mapObject.msgReceiveCount);
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
			// Synchronizing for multi-thread access to mapObject
			synchronized(mapObject){
   				//If AppMsg and node is passive becomes active only if
				//it has sent fewer than maxNumber messages
				if((msg instanceof ApplicatonMessage) && 
						(!mapObject.active) && 
						(mapObject.msgSentCount < mapObject.maxNumber)&& (!mapObject.saveLocalState) )  //&& (!mapObject.saveLocalState)
				{
			
					ApplicatonMessage tmp = (ApplicatonMessage) msg;
					mapObject.active = true;
					mapObject.msgReceiveCount++;
					writeMsgtofile(tmp);
					new SendMessageClass(mapObject).start();
				}
				else if((msg instanceof ApplicatonMessage) && (!mapObject.active) && (mapObject.saveLocalState)) {
					
					mapObject.msgReceiveCount++;
					int channelNo = ((ApplicatonMessage)msg).nodeId;
					SnapshotProtocolClass.saveAppMsg(channelNo,(ApplicatonMessage)msg,mapObject);
					
				}
				else if(msg instanceof MarkerMsg) {
					int channelNo = ((MarkerMsg)msg).nodeId;
					System.out.println("\nMarker Msg Received\n");
					SnapshotProtocolClass.sendMarketMsg(mapObject,channelNo);
				}
				
				else if(msg instanceof StateMsg) {
					
					
					System.out.println("\nState Msg received\n");
					if(mapObject.id == 0) {
						
						mapObject.StateMsgList.put(((StateMsg)msg).nodeId, true);
						
						mapObject.nodesLocalState.put(((StateMsg)msg).nodeId, ((StateMsg)msg).currnodestate);
						
						mapObject.nodemsgStatus.put(((StateMsg)msg).nodeId, ((StateMsg)msg).nodeStatus);
						//mapObject.AppMsgList.get(((StateMsg)msg).nodeId).add(msg);
						
						if (mapObject.StateMsgList.size() == mapObject.numOfNodes-1) {
							//Detect Termination
							System.out.println("\nAll state messages received checking for nodes active status\n");
							
							boolean restart = false;
							
							restart = SnapshotProtocolClass.Detecttermination(mapObject);
							
							if(restart) {
								mapObject.setValues(mapObject);
								new SnapshotHandlerClass(mapObject).start();
							}
							else {
								System.out.println("\nAll nodes are passive, we can terminate!!!\n");
								//System.exit(3);
								writeMsgtofile(new TerminateMsg());
								SnapshotProtocolClass.sendTerminateMsg(mapObject);
							}
							
						}
						
					}
					else {
						SnapshotProtocolClass.sendToSource(mapObject,(StateMsg)msg);
					}
					
				}
				else if(msg instanceof TerminateMsg) {
					writeMsgtofile(new TerminateMsg());
					SnapshotProtocolClass.sendTerminateMsg(mapObject);
				}
				else {
					mapObject.msgReceiveCount++;
				}
				
				if(msg instanceof ApplicatonMessage){
					
					//Implementing vector protocol on receiver side
					for(int i=0;i<mapObject.numOfNodes;i++){
						mapObject.vectorClock[i] = Math.max(mapObject.vectorClock[i], ((ApplicatonMessage) msg).vectorClock[i]);
					}
					mapObject.vectorClock[mapObject.id]++;
					
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
