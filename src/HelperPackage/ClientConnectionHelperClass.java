package HelperPackage;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ClientConnectionHelperClass {
	
	private InetAddress addressResolution(String hostname){
		InetAddress address = null;
		try {
			address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return address;
	}
	
	private SocketClient connection(String address, port){
		Socket client = null;
		try {
			client = new Socket(address,port);		
			System.out.println("Client Connection Achieved(address,port):"+address.getHostAddress()+" "+port);
		} catch (IOException e) {
			System.out.println("Connection Broken");
			e.printStackTrace();
			System.exit(1);
		}
		return client;
	}
	
	private ObjectOutputStream oos writing(String client.getOutputStream())
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
					
			e.printStackTrace();
		}
		return oos;
	}
	//Each node acts as a client to all its neighboring nodes
	public ClientConnectionHelperClass(ConfigStructure mapObject, int curNode) {
		for(int i=0;i<mapObject.numOfNodes;i++){
			if(mapObject.adjMtx[curNode][i] == 1){
				String hostName = mapObject.nodeInfo.get(i).host;
				int port = mapObject.nodeInfo.get(i).port;
				
				
// 				InetAddress address = null;
// 				try {
// 					address = InetAddress.getByName(hostName);
// 				} catch (UnknownHostException e) {
// 					e.printStackTrace();
// 					System.exit(1);
// 				}
				
				
//				try {
//					address = InetAddress.getLocalHost();
//				}
//				catch(Exception e){
//					e.printStackTrace();
//					System.exit(1);
//				}
				
				
// 				Socket client = null;
// 				try {
// 					client = new Socket(address,port);
// 					System.out.println("Client Connection Achieved(address,port):"+address.getHostAddress()+" "+port);
// 				} catch (IOException e) {
// 					System.out.println("Connection Broken");
// 					e.printStackTrace();
// 					System.exit(1);
				}
				//Send client request to all neighboring nodes
				
				InetAddress address = addressResolution(hostname);
				SocketClient = connection(String address, port);
			
				mapObject.channels.put(i, client);
				mapObject.neighbors.add(i);
				ObjectOutputStream oos =  writing(String client.getOutputStream());
				
				
			
// 				ObjectOutputStream oos = null;
// 				try {
// 					oos = new ObjectOutputStream(client.getOutputStream());
// 				} catch (IOException e) {
					
// 					e.printStackTrace();
// 				}
				mapObject.oStream.put(i, oos);	
// 			}
		}
		System.out.println("Sample");
	}
}
