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

	public ReceiveMessageClass(Socket ClientSocket,ConfigStructure mapObject) {
		this.socket = ClientSocket;
		this.mapObject = mapObject;
	}
	
	public void writeMsgtofile(ApplicatonMessage tmp) {
		String OutFileName = ConfigStructure.outFile + "-" + mapObject.id + ".out";
		
		mapObject.active = true; 
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
	
	public void MessageListener(ObjectInputStream ois) {
		try {
			MessageStructure msg;
			msg = (MessageStructure) ois.readObject();
			// Synchronizing for multi-thread access to mapObject
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
					System.out.println("All messages have been sent, for logs check the .out files");
					//System.exit(3);
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
