package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import resource.Factory;
import utilities.Util;

public class ServerListener extends Thread {

	private ServerSocket ss;
	private Vector<ServerClientCommunicator> sccVector;
	private Factory factory;
	private FactoryWarehouse factoryWarehouse;
	
	public ServerListener(ServerSocket ss) {
		this.ss = ss;
		sccVector = new Vector<ServerClientCommunicator>();
		factoryWarehouse = new FactoryWarehouse(sccVector);
		new Thread(factoryWarehouse).start();
	}
	
	public void sendFactory(Factory factory) {
		this.factory = factory;
		for (ServerClientCommunicator scc : sccVector) {
			scc.sendFactory(factory);
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
						scc.sendFactory(factory);
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
}
