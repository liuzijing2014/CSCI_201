package server;

import java.net.ServerSocket;

import resource.Factory;

public class FactoryServer {

	private ServerSocket ss;
	private static ServerListener serverListener;
	
	public FactoryServer() {
		PortGUI pf = new PortGUI();
		ss = pf.getServerSocket();
		new FactoryServerGUI();
		listenForConnections();
	}
	
	private void listenForConnections() {
		serverListener = new ServerListener(ss);
		serverListener.start();
	}
	
	public static void sendFactory(Factory factory) {
		if (serverListener != null) {
			serverListener.sendFactory(factory);
		}
	}
		
	public static void main(String [] args) {
		new FactoryServer();
	}
}
