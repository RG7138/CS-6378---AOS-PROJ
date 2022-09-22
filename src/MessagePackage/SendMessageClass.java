package MessagePackage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import HelperPackage.ConfigStructure;

public class SendMessageClass extends Thread{
	ConfigStructure mapObject;
	public SendMessageClass(ConfigStructure mapObject) {
		this.mapObject = mapObject;
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

		//Send the messages to random neighbors each time and add minSendDelay between each send
		for(int i=0;i<randMessages;i++){
			synchronized(mapObject){
				//get a random neigbour
				int randNeighborNode = this.getRandomNumber(0,mapObject.neighbors.size()-1);
				int curNeighbor = mapObject.neighbors.get(randNeighborNode);

				if(mapObject.active == true){
					//send application message
					AppMessage m = new AppMessage(); 
					// Implementing Vector clock protocol
					
					m.nodeId = mapObject.id;
			
					//Send object data to the neighbor
					try {
						ObjectOutputStream oos = mapObject.oStream.get(curNeighbor);
						oos.writeObject(m);	
						oos.flush();
						System.out.println("Message -" + m.msg + " sent to Node:"+ curNeighbor);
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
			catch (InterruptedException e) {
				System.out.println("Error in SendMessages");
				e.printStackTrace();
			}
		}
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
