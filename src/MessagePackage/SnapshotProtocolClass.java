package MessagePackage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import HelperPackage.ConfigStructure;
import HelperPackage.SpanningTreeHelperClass;
import MessagePackage.MessageStructure.ApplicatonMessage;
import MessagePackage.MessageStructure.MarkerMsg;
import MessagePackage.MessageStructure.MessageStructure;
import MessagePackage.MessageStructure.StateMsg;
import MessagePackage.MessageStructure.TerminateMsg;
import HelperPackage.SpanningTreeHelperClass;

public class SnapshotProtocolClass {
	
	public static void iniateSnapshot(ConfigStructure obj) {
		synchronized(obj){
			
			sendMarketMsg(obj,obj.id);
		}
	}
	
	public static void writeToFile(ConfigStructure obj) {
		String OutFileName = ConfigStructure.outFile + "-" + obj.id + ".out";
		
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
			
			bufferedWriter.write("\nSnap Count - " + obj.Snapcount + "\n\n");
			
			
			bufferedWriter.write("\nLocal Vector Clock-\n");
			for(var i:obj.vectorClock) {
				bufferedWriter.write(i+" ");
			}
			
			bufferedWriter.write("\nTotal Application Messages Sent - \n" + obj.msgSentCount + "\nTotal Application Messages Received - \n" + obj.msgReceiveCount);
			bufferedWriter.close();
			
		}
		catch(Exception e) {
			System.out.println("Error: Unable to write to file '" + OutFileName + "'");
			e.printStackTrace();
		}
	}
	
	public static void sendTerminateMsg(ConfigStructure obj) {
		
		synchronized (obj) {
			
			for(var i:obj.neighbors) {
				
				TerminateMsg msg = new TerminateMsg();
				
				ObjectOutputStream oos = obj.oStream.get(i);
				
				try {
					oos.writeObject(msg);
					oos.flush();
				}
				catch(Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
			
		}
		
	}
	
	public static void sendMarketMsg(ConfigStructure obj,int id) {
		synchronized (obj) {
			
			if (obj.MarkerSent == false) {
				
				obj.saveLocalState = true;
				obj.MarkerSent = true;
				obj.MarkerMsgList.put(id,true);
				
				int[] vectorCopy = new int[obj.vectorClock.length];
				
				for(int i=0;i<vectorCopy.length;++i) {
					vectorCopy[i] = obj.vectorClock[i];
				}
				
				obj.Snapshots.add(vectorCopy);
				
				for(int i=0;i<obj.neighbors.size();++i) {
					
					MarkerMsg msg = new MarkerMsg();
					
					msg.nodeId = obj.id;
					
					
					ObjectOutputStream oos = obj.oStream.get(obj.neighbors.get(i));
					
					try {
						oos.writeObject(msg);
						oos.flush();
					}
					catch(Exception e) {
						
						e.printStackTrace();
						
					}
					
				}
				
			}
			else {
				
				//obj.saveLocalState = false;
				obj.MarkerMsgList.put(id,true);
				
				int countvar = 0;
				
				for(int i=0;i<obj.neighbors.size();++i) {
					if(obj.MarkerMsgList.get(obj.neighbors.get(i)) == true) {
						countvar++;
					}
				}
				
				if(countvar == obj.neighbors.size()) {
					
					if(obj.id == 0) {
						
						obj.MarkerSent = false;
						obj.saveLocalState = false;
						obj.AppMsgList = obj.AppMsgList;
					}
					else {
						
						obj.Snapcount++;
						int parentid = SpanningTreeHelperClass.getParent(obj.id);
						
						ObjectOutputStream oos = obj.oStream.get(parentid);
						
						try {
							StateMsg msg = new StateMsg();
							
							msg.nodeId = obj.id;
							
							for(var i:obj.AppMsgList.keySet()) {
								
								if(!obj.AppMsgList.get(i).isEmpty()) {
							
									msg.nodeStatus = true;
									break;
								}
								
							}
							
							msg.currnodestate = obj.active;
							
							System.out.println("\nSending out state message\n");
							
//							try {
//								writeToFile(obj);
//							}
//							catch(Exception e) {
//								
//							}
							
							oos.writeObject(msg);
							
							oos.flush();
							
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						
						obj.MarkerSent = false;
						obj.saveLocalState = false;
						
						obj.setValues(obj);
					}
					
				}
				
			}
			
		}
	}
	
	public static boolean Detecttermination(ConfigStructure obj) {
		synchronized (obj) {
				
				//checking id all nodes are passive
				for(var i:obj.nodesLocalState.keySet()) {
		
					if (obj.nodesLocalState.get(i)) {
						return true;
					}
		
				}
				
				//Checking if all AppMsgLists are empty
				for(var i : obj.nodemsgStatus.keySet()) {
					
					if(obj.nodemsgStatus.get(i)) {
						return true;
					}
					
				}
		}
		
		return false;
		
	}
	
	public static void saveAppMsg(int nodeId,ApplicatonMessage msg,ConfigStructure obj) {
		
		synchronized (obj) {
			
			if(obj.MarkerMsgList.get(nodeId) == false) {
				
				if((obj.AppMsgList.get(nodeId).isEmpty())){
					ArrayList<ApplicatonMessage> msgList = obj.AppMsgList.get(nodeId);
					msgList.add(msg);
					obj.AppMsgList.put(nodeId, msgList); // add to Hash map
				}
				// if the ArrayList is already there just add this message to it
				else if(!(obj.AppMsgList.get(nodeId).isEmpty())){
					obj.AppMsgList.get(nodeId).add(msg);
				}
				
			}
			
		}
		
	}
	
	public static void sendToSource(ConfigStructure obj,StateMsg msg) {
		
		synchronized (obj) {
			int parentid = SpanningTreeHelperClass.getParent(obj.id);
				
			ObjectOutputStream oos = obj.oStream.get(parentid);
			
			try {
				oos.writeObject(msg);
				oos.flush();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
			
			
			
	} 
	
}
