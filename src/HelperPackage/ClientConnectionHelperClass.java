package HelperPackage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnectionHelperClass {
	
	private InetAddress addressResolution(String hostName) {
		
		InetAddress address = null;
		
		try {
			address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return address;
	}
		
	private Socket connection(InetAddress address,int port) {
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
		
	private ObjectOutputStream ooswriting(Socket client) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {

			e.printStackTrace();
		}
		return oos;
	}
	
	//Each node acts as a client to all its neighboring nodes
	public ClientConnectionHelperClass(ConfigStructure mapObject, int currentNode) {
		for(int i=0;i<mapObject.numOfNodes;i++){
			if(mapObject.adjacent[currentNode][i] == 1){
				String hostName = mapObject.information.get(i).host;
				int port = mapObject.information.get(i).port;
				InetAddress address = null;
				
				address = addressResolution(hostName);
				

				Socket client = null;
				
				client = connection(address,port);
				//Send client request to all neighboring nodes
				mapObject.channels.put(i, client);
				mapObject.neighbors.add(i);
				ObjectOutputStream oos = null;
				
				oos = ooswriting(client);
				
				mapObject.oStream.put(i, oos);	
			}
		}
		System.out.println("Sample");
	}
}
