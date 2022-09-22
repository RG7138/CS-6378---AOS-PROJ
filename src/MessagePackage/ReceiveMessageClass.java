package MessagePackage;

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
					if((msg instanceof AppMessage) && 
							(mapObject.active == false) && 
							(mapObject.msgSentCount < mapObject.maxNumber))
					{
						
						AppMessage tmp = (AppMessage) msg;
						
						mapObject.active = true; 
						
						System.out.println(tmp.msg+" received from Node:"+ tmp.nodeId);
						
						new SendMessageClass(mapObject).start();
					}
				}
			}
			catch(StreamCorruptedException e) {
				e.printStackTrace();
				System.exit(2);
			}
			catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(2);
			}
		}
	}
}
