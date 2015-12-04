package server;

import java.net.ServerSocket;
import java.util.ArrayList;

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
	
	public static void sendFactory(Factory factory, ArrayList<Factory> splitFactories) {
		MySQLDriver msql = new MySQLDriver();
		msql.Connect();
		for(Product p : factory.getProducts()) {
			if(!msql.doesExist(p.getName())) {
				msql.Add(p.getName());
			}
		}
		msql.Stop();
		
		if (serverListener != null) {
			serverListener.sendFactories(factory, splitFactories);
		}
	}
	
	public static void updateResourceList(String productName) {
		MySQLDriver msql = new MySQLDriver();
		msql.Connect();
		msql.incrementCreatedCount(productName);
		msql.Stop();		
	}
		
	public static void main(String [] args) {
		new FactoryServer();
	}
}
