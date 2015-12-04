package server;

import java.net.ServerSocket;

import resource.Factory;
import resource.Product;

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
		MySQLDriver msql = new MySQLDriver();
		msql.connect();
		for(Product p: factory.getProducts()){
			if(!msql.doesExist(p.getName())){
				msql.add(p.getName());
			}
		}
		msql.stop();
		
		if (serverListener != null) {
			serverListener.sendFactory(factory);
		}
	}
		
	public static void main(String [] args) {
		new FactoryServer();
	}
}
