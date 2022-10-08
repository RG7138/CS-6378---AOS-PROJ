package MessagePackage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import HelperPackage.ConfigStructure;
import HelperPackage.SpanningTreeHelperClass;
import MessagePackage.MessageStructure.ApplicationMessage;
import MessagePackage.MessageStructure.MarkerMsg;
import MessagePackage.MessageStructure.MessageStructure;
import MessagePackage.MessageStructure.StateMsg;
import MessagePackage.MessageStructure.TerminateMsg;
import HelperPackage.SpanningTreeHelperClass;

public class SnapshotProtocolClass {
	
	public static void iniateSnapshot(ConfigStructure obj) {
		synchronized(obj){
			
			sendMarkerMsg(obj,obj.id);
		}
	}
	
	public static void writeToFile(ConfigStructure obj) {
		String OutFileName = ConfigStructure.outFile + "-" + obj.id + ".out";
		
		try {
			File file = new File(OutFileName);
			FileWriter fileWriter;
			if(file.exists()){
				fileWriter = new FileWriter(file,false);
			}
			else
			{
				fileWriter = new FileWriter(file,false);
			}
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write("\nSnap Count - " + obj.Snapcount + "\n\n");
			
			
			bufferedWriter.write("\nLocal Vector Clock-\n");
			for(int i:obj.vectorClock) {
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
	
	//Node 0 has checked that all other nodes in the distributed system are passive
	//and there are no intransit messages
	public static void sendTerminateMsg(ConfigStructure obj) {
		
		synchronized (obj) {
			
			for(int i:obj.neighbors) {
				
				TerminateMsg msg = new TerminateMsg();
				
				ObjectOutputStream oos = obj.outStream.get(i);
				
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
	
	//Node on receiving markers sends marker message if not previously sent to neighbours
	public static void sendMarkerMsg(ConfigStructure obj,int id) {
		synchronized (obj) {
			
			if (obj.MarkerSent == false) {
				
				obj.saveLocalState = true;
				obj.MarkerSent = true;
				obj.MarkerMsgList.put(id,true);
				
				int[] localvectorclk = new int[obj.vectorClock.length];
				
				for(int i=0;i<localvectorclk.length;++i) {
					localvectorclk[i] = obj.vectorClock[i];
				}
				
				obj.Snapshots.add(localvectorclk);
				
				for(int i=0;i<obj.neighbors.size();++i) {
					
					MarkerMsg msg = new MarkerMsg();
					
					msg.nodeId = obj.id;
					
					
					ObjectOutputStream oos = obj.outStream.get(obj.neighbors.get(i));
					
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
						int parentid = SpanningTreeHelperClass.returnParent(obj.id);
						
						ObjectOutputStream oos = obj.outStream.get(parentid);
						
						try {
							StateMsg msg = new StateMsg();
							
							msg.nodeId = obj.id;
							
							for(int i:obj.AppMsgList.keySet()) {
								
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
	
	//Detect Snapsot protocol termination
	public static boolean Detecttermination(ConfigStructure obj) {
		synchronized (obj) {
				
				//checking if all nodes are passive
				for(int i:obj.nodesLocalState.keySet()) {
		
					if (obj.nodesLocalState.get(i)) {
						return true;
					}
		
				}
				
				//Checking if all AppMsgLists are empty i.e there are no in-tranist messages
				for(int i : obj.nodemsgStatus.keySet()) {
					
					if(obj.nodemsgStatus.get(i)) {
						return true;
					}
					
				}
		}
		
		return false;
		
	}
	
	//updates application message list for every node
	public static void saveAppMsg(int nodeId,ApplicationMessage msg,ConfigStructure obj) {
		
		synchronized (obj) {
			
			if(obj.MarkerMsgList.get(nodeId) == false) {
				
				if((obj.AppMsgList.get(nodeId).isEmpty())){
					ArrayList<ApplicationMessage> msgList = obj.AppMsgList.get(nodeId);
					msgList.add(msg);
					obj.AppMsgList.put(nodeId, msgList); // update node object
				}
				// Update the MsgList if it already exists
				else if(!(obj.AppMsgList.get(nodeId).isEmpty())){
					obj.AppMsgList.get(nodeId).add(msg);
				}
				
			}
			
		}
		
	}
	
	//propogates state message to parent eventually reaching Node 0
	public static void sendToSource(ConfigStructure obj,StateMsg msg) {
		
		synchronized (obj) {
			int parentid = SpanningTreeHelperClass.returnParent(obj.id);
				
			ObjectOutputStream oos = obj.outStream.get(parentid);
			
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
