package HelperPackage;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHelperClass{

	ServerSocket listener = null;
	Socket socket = null;
	int serverPort;
	private ConfigStructure mapObject;
	InetAddress addr;
	InetAddress addr1;
	InetAddress addr2;
	
	public ServerConnectionHelperClass(ConfigStructure mapObject) {
		
		this.mapObject = mapObject; //Global mapObject
		// port number on which this node should listen 
		serverPort = mapObject.nodes.get(mapObject.id).port;
		String host = mapObject.nodes.get(mapObject.id).host;
		try {
			
			addr = InetAddress.getByName(mapObject.nodes.get(mapObject.id).host);
			
			System.out.println(serverPort);
			System.out.println(addr);
			
			listener = new ServerSocket(serverPort,-1,InetAddress.getByName(mapObject.nodes.get(mapObject.id).host));
			
			addr = listener.getInetAddress();
//			
			addr1 = InetAddress.getLocalHost();
//			
			addr2 = InetAddress.getByName("localhost");
		} 
		catch(BindException e) {
			System.out.println("Server Conn failed on Node" + mapObject.id + " : " + e.getMessage() + ", Port : " + serverPort);
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	public void listenforinput(){
		//Listen and accept for any client connections
		try {
			while (true) {
				try {
					socket = listener.accept();
				} catch (IOException e1) {
					System.out.println("Connection Broken");
					System.exit(1);
				}
				// For every client request start a new thread 
				//new ReceiveThread(socket,mapObject).start();
			}
		}
		finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}