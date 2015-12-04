package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import resource.Factory;
import resource.Product;
import utilities.Util;

public class ServerListener extends Thread {

	private ServerSocket ss;
	private Vector<ServerClientCommunicator> sccVector;
	private Factory factory;
	private ArrayList<Factory> factories;
	private FactoryWarehouse factoryWarehouse;
	public ServerListener(ServerSocket ss) {
		this.ss = ss;
		sccVector = new Vector<ServerClientCommunicator>();
		factoryWarehouse = new FactoryWarehouse(sccVector);
		new Thread(factoryWarehouse).start();
	}
	
	public void sendFactories(Factory factory, ArrayList<Factory> factories) {
		this.factory = factory;
		this.factories = factories;
		for (int i = 0; i < sccVector.size(); ++i) {
			if(i < factories.size()) {
				sccVector.elementAt(i).sendFactory(factories.get(i));
			}
			else {
				Factory f = new Factory(factory.getName(), factory.getNumberOfWorkers(), factory.getWidth(), factory.getHeight(), factory.getResources(), new Vector<Product>());
				f.setTaskBoardLocation(factory.getTaskBoardLocation());
				sccVector.elementAt(i).sendFactory(f);
			}
		}
		factoryWarehouse.setFactory(factory);
	}
	
	public void removeServerClientCommunicator(ServerClientCommunicator scc) {
		sccVector.remove(scc);
	}
	
	public void run() {
		try {
			FactoryServerGUI.addMessage(Constants.initialFactoryTextAreaString + ss.getLocalPort());
			while(true) {
				Socket s = ss.accept();
				FactoryServerGUI.addMessage(Constants.startClientConnectedString + s.getInetAddress() + Constants.endClientConnectedString);
				
				try {
					// this line can throw an IOException
					// if it does, we won't start the thread
					ServerClientCommunicator scc = new ServerClientCommunicator(s, this);
					scc.start();
					sccVector.add(scc);
					
					// right when a client connects, if there is already a factory loaded on the server, send it out
					if (factory != null) {
						if(sccVector.size() <= factories.size()) {
							scc.sendFactory(factories.get(sccVector.size()-1));
						}
						else {
							Factory f = new Factory(factory.getName(), factory.getNumberOfWorkers(), factory.getWidth(), factory.getHeight(), factory.getResources(), new Vector<Product>());
							f.setTaskBoardLocation(factory.getTaskBoardLocation());
							scc.sendFactory(f);
						}
					}
				} catch (IOException ioe) {
					Util.printExceptionToCommand(ioe);
				}
			}
		} catch(BindException be) {
			Util.printExceptionToCommand(be);
		}
		catch (IOException ioe) {
			Util.printExceptionToCommand(ioe); 
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					Util.printExceptionToCommand(ioe);
				}
			}
		}
	}
	
	public FactoryWarehouse getFactoryWarehouse() {
		return factoryWarehouse;
	}
}
