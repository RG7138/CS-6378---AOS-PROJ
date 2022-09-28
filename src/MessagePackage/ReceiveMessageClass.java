package MessagePackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import HelperPackage.ConfigStructure;

//Read object data sent by neighboring clients
public class ReceiveMessageClass extends Thread {
	Socket socket;
	ConfigStructure mapObject;

	public ReceiveMessageClass(Socket csocket,ConfigStructure mapObject) {
		this.socket = csocket;
		this.mapObject = mapObject;
	}
	
	public void writeMsgtofile(ApplicatonMessage tmp) {
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
			
			bW.write("\n"+"'"+ tmp.msg +"'"+" received from Node:"+ tmp.nodeId+"\n\n");
			
			bW.close();
			
		}
		catch(Exception e) {
			System.out.println("Error writing to file '" + fileName + "'");
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
			try {
				MessageStructure msg;
				msg = (MessageStructure) ois.readObject();
				// Synchronizing mapObject so that multiple threads access mapObject in a synchronized way
				synchronized(mapObject){
					
       				//If AppMsg and node is passive becomes active only if
					//it has sent fewer than maxNumber messages
					if((msg instanceof ApplicatonMessage) && 
							(mapObject.active == false) && 
							(mapObject.msgSentCount < mapObject.maxNumber))
					{
						
						ApplicatonMessage tmp = (ApplicatonMessage) msg;
						
						mapObject.active = true; 
						
						writeMsgtofile(tmp);
						
						new SendMessageClass(mapObject).start();
					}
					else if(mapObject.msgSentCount == mapObject.maxNumber) {
						System.out.println("Max messages sent please check the .out files for logs");
						//System.exit(3);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
