package HelperPackage;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import MessagePackage.ReceiveMessageClass;

public class ServerConnectionHelperClass{

	ServerSocket listener = null;
	Socket socket = null;
	int serverPort;
	private ConfigStructure nodeObj;
	InetAddress addr;
	InetAddress addr1;
	InetAddress addr2;
	
	public void AcceptClientConnections(){
		
		try {
			while (true) {
				try {
					socket = listener.accept();
				} catch (IOException e1) {
					System.out.println("Connection Broken");
					System.exit(1);
				}
				new ReceiveMessageClass(socket,nodeObj).start();
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
			
			listener = new ServerSocket(serverPort,-1,InetAddress.getByName(nodeObj.nodes.get(nodeObj.id).host));
			System.out.println(serverPort);
			addr = listener.getInetAddress();
			System.out.println(addr);
		}
		catch(BindException e) {
			System.out.println("Failed Server Connection on Node" + nodeObj.id + " : " + e.getMessage() + ", Port : " + serverPort);
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
	
	public ServerConnectionHelperClass(ConfigStructure nodeObj) {
		
		this.nodeObj = nodeObj; 
		
		serverPort = nodeObj.nodes.get(nodeObj.id).portno;
		String host = nodeObj.nodes.get(nodeObj.id).host;
		
		//ServerSocket listener = null;
		listener = CreateServer(serverPort, host);
		
		ServerSleep(10000);
	}
	
	
}