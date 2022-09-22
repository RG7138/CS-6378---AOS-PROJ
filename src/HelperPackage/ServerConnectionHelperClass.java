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
	
	public void AcceptClientConnections(){
		//Listen for client requests and accept connections
		try {
			while (true) {
				try {
					socket = listener.accept();
				} catch (IOException e1) {
					System.out.println("Connection Broken");
					System.exit(1);
				}
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
	
	private ServerSocket CreateServer(int serverPort, String host) {
		
		try {
			//System.out.println(serverPort);
			//addr = listener.getInetAddress();
			//System.out.println(addr);
			listener = new ServerSocket(serverPort,-1,InetAddress.getByName(mapObject.nodes.get(mapObject.id).host));
		}
		catch(BindException e) {
			System.out.println("Failed Server Connection on Node" + mapObject.id + " : " + e.getMessage() + ", Port : " + serverPort);
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return listener;
	}
	
	private void ServerSleep(int millisecs) {
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public ServerConnectionHelperClass(ConfigStructure mapObject) {
		
		this.mapObject = mapObject; //Global mapObject
		// port number on which this node should listen 
		serverPort = mapObject.nodes.get(mapObject.id).port;
		String host = mapObject.nodes.get(mapObject.id).host;
		
		ServerSocket listener = null;
		listener = CreateServer(serverPort, host);
		
		ServerSleep(10000);
	}
	
	
}