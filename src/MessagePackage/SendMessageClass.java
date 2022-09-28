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
			synchronized(mapObject){
				
				//get a random neigbour id to send message
				int NeighborNodeID = this.getRandomNumber(0,mapObject.neighbors.size()-1);
				int curNeighbor = mapObject.neighbors.get(NeighborNodeID);

				if(mapObject.active == true){
					//sending application message
					ApplicatonMessage appmsg = new ApplicatonMessage(); 

					appmsg.nodeId = mapObject.id;

					//Send message to the neighbor
					try {
						writeMsgtofile(appmsg,curNeighbor);

						//System.out.println("Message -" +"'"+ m.msg  +"'"+ " sent to Node:"+ curNeighbor+"\n");

						//Writing to out stream object
						ObjectOutputStream oos = mapObject.oStream.get(curNeighbor);
						oos.writeObject(appmsg);	
						oos.flush();

					} catch (IOException e) {
						e.printStackTrace();
					}	
					
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

		// to get a random number between minPerActive to maxPerActive to send that many messages
		int MessagesCount = -1;
		int minSendDelay = 0;
		synchronized(mapObject){
			MessagesCount = this.getRandomNumber(mapObject.minPerActive,mapObject.maxPerActive);
			
			// If random number is 0 we again try to get a random number, as we can not have 0 as the number of messages to be sent
			if(MessagesCount == 0){
				MessagesCount = this.getRandomNumber(mapObject.minPerActive + 1,mapObject.maxPerActive);
			}
			minSendDelay = mapObject.minSendDelay;
		}
		
		sendMsgstoNeighbours(minSendDelay,MessagesCount);
		
		synchronized(mapObject){
			//  Nodes becomes passive after sending messages
			mapObject.active = false;
		}


	}
	public void run(){
		try {
			this.sendMessages();
		} catch (Exception e) {
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
