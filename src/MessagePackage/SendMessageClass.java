package MessagePackage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import HelperPackage.ConfigStructure;
import MessagePackage.MessageStructure.ApplicationMessage;


public class SendMessageClass extends Thread{
	ConfigStructure nodeObj;
	public SendMessageClass(ConfigStructure nodeObj) {
		this.nodeObj = nodeObj;
	}
	
	public void writeMsgtofile(ApplicationMessage tmp,int curNeighbor) {
		String fileName = ConfigStructure.outFile + "-" + nodeObj.id + ".out";
		
		nodeObj.active = true; 
		try {
			File file = new File(fileName);
			FileWriter fileWriter;
			if(file.exists()){
				fileWriter = new FileWriter(file,true);
			}
			else
			{
				fileWriter = new FileWriter(file);
			}
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write("Message -" +"'"+ tmp.msg  +"'" + " sent to Node:"+ curNeighbor+"\n");
			
			bufferedWriter.close();
			
		}
		catch(Exception e) {
			System.out.println("Error writing to file '" + fileName + "'");
		}
		
	}
	
	public void sendMsgstoNeighbours(int minSendDelay,int MessagesCount) {
		
		for(int i=0;i<MessagesCount;i++){
			synchronized(nodeObj){
				
				//get a random neighbor id to send message
				int NeighborNodeID = this.randomValueGenerator(0,nodeObj.neighbors.size()-1);
				int curNeighbor = nodeObj.neighbors.get(NeighborNodeID);

				if(nodeObj.active == true){
					//sending application message and updating vector clock
					
					nodeObj.vectorClock[nodeObj.id]++;
					
					ApplicationMessage appmsg = new ApplicationMessage(); 

					appmsg.nodeId = nodeObj.id;
					
					appmsg.vectorClock = new int[nodeObj.vectorClock.length];
					System.arraycopy( nodeObj.vectorClock, 0, appmsg.vectorClock, 0, nodeObj.vectorClock.length );
					
					//Send message to the neighbor
					try {
						writeMsgtofile(appmsg,curNeighbor);

						//System.out.println("Message -" +"'"+ m.msg  +"'"+ " sent to Node:"+ curNeighbor+"\n");

						//Writing to out stream object
						ObjectOutputStream oos = nodeObj.outStream.get(curNeighbor);
						oos.writeObject(appmsg);	
						oos.flush();

					} catch (IOException e) {
						e.printStackTrace();
					}	
					
					nodeObj.msgSentCount++;
				}
			}
			
			// Wait for minimum sending delay before sending another message
			try{
				Thread.sleep(minSendDelay);
			}
			catch (Exception e) {
				System.out.println("Unable to send Messages to neighbors");
				e.printStackTrace();
			}
		}
	}
	
	
	void sendMessages() throws InterruptedException{

		// to get a random number between minPerActive and maxPerActive 
		int MessagesCount = -1;
		int minSendDelay = 0;
		synchronized(nodeObj){
			MessagesCount = this.randomValueGenerator(nodeObj.minPerActive,nodeObj.maxPerActive);
			minSendDelay = nodeObj.minSendDelay;
		}
		
		sendMsgstoNeighbours(minSendDelay,MessagesCount);
		
		
		synchronized(nodeObj){
			nodeObj.active = false;
		}


	}
	public void run(){
		try {
			this.sendMessages();
		} catch (Exception e) {
			System.out.println("Unable to send Messages to neighbors");
			e.printStackTrace();
		}
	}
	
	int randomValueGenerator(int min,int max){
		Random rand = new Random();
		
		// generates a random value between 0 and (max-min)+1.
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
