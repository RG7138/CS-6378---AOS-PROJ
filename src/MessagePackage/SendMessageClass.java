package MessagePackage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import HelperPackage.ConfigStructure;

public class SendMessageClass extends Thread{
	ConfigStructure mapObject;
	public SendMessageClass(ConfigStructure mapObject) {
		this.mapObject = mapObject;
	}
	
	public void writeMsgtofile(ApplicatonMessage tmp,int curNeighbor) {
		String fileName = ConfigStructure.outFile + "-" + mapObject.id + ".out";
		
		mapObject.active = true; 
		try {
			File file = new File(fileName);
			FileWriter fW;
			if(file.exists()){
				fW = new FileWriter(file,true);
			}
			else
			{
				fW = new FileWriter(file);
			}
			BufferedWriter bW = new BufferedWriter(fW);
			
			bW.write("Message -" +"'"+ tmp.msg  +"'" + " sent to Node:"+ curNeighbor+"\n");
			
			bW.close();
			
		}
		catch(Exception e) {
			System.out.println("Error writing to file '" + fileName + "'");
		}
		
	}
	
	public void sendMsgstoNeighbours(int minSendDelay,int randMessages) {
		//Send the messages to random neighbors each time and add minSendDelay between each send
				for(int i=0;i<randMessages;i++){
					synchronized(mapObject){
						//get a random neigbour
						int randNeighborNode = this.getRandomNumber(0,mapObject.neighbors.size()-1);
						int curNeighbor = mapObject.neighbors.get(randNeighborNode);

						if(mapObject.active == true){
							//send application message
							ApplicatonMessage m = new ApplicatonMessage(); 
							// Implementing Vector clock protocol
							
							String fileName = ConfigStructure.outFile + "-" + mapObject.id + ".out";
							m.nodeId = mapObject.id;
					
							//Send object data to the neighbor
							try {
								writeMsgtofile(m,curNeighbor);
								
								//System.out.println("Message -" +"'"+ m.msg  +"'"+ " sent to Node:"+ curNeighbor+"\n");
								
								ObjectOutputStream oos = mapObject.oStream.get(curNeighbor);
								oos.writeObject(m);	
								oos.flush();
								
							} catch (IOException e) {
								e.printStackTrace();
							}	
							//increment msgSentCount
							mapObject.msgSentCount++;
						}
					}
					// Wait for minimum sending delay before sending another message
					try{
						Thread.sleep(minSendDelay);
					}
					catch (Exception e) {
						System.out.println("Error in SendMessages");
						e.printStackTrace();
					}
				}
	}
	
	
	void sendMessages() throws InterruptedException{

		// get a random number between minPerActive to maxPerActive to send that many messages
		int randMessages = 1;
		int minSendDelay = 0;
		synchronized(mapObject){
			randMessages = this.getRandomNumber(mapObject.minPerActive,mapObject.maxPerActive);
			// If random number is 0
			if(randMessages == 0){
				randMessages = this.getRandomNumber(mapObject.minPerActive + 1,mapObject.maxPerActive);
			}
			minSendDelay = mapObject.minSendDelay;
		}
		
		sendMsgstoNeighbours(minSendDelay,randMessages);
		
		synchronized(mapObject){
			// After sending minPerActive to maxPerActive number of messages node should be passive
			mapObject.active = false;
		}


	}
	public void run(){
		try {
			this.sendMessages();
		} catch (InterruptedException e) {
			System.out.println("Error in SendMessages");
			e.printStackTrace();
		}
	}
	// Function to generate random number in a given range
	int getRandomNumber(int min,int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
