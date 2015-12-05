package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import resource.Factory;
import resource.Product;
import utilities.Util;

public class FactoryClientListener extends Thread {

	private Socket mSocket;
	private ObjectInputStream ois;
	private PrintWriter pw;
	private FactoryManager mFManager;
	private FactoryClientGUI mFClientGUI;
	
	private Queue<Product> toSend;
	
	public FactoryClientListener(FactoryManager inFManager, FactoryClientGUI inFClientGUI, Socket inSocket) {
		mSocket = inSocket;
		mFManager = inFManager;
		mFClientGUI = inFClientGUI;
		toSend = new LinkedBlockingQueue<Product>();
		boolean socketReady = initializeVariables();
		if (socketReady) {
			start();
		}
	}

	private boolean initializeVariables() {
		try {
			ois = new ObjectInputStream(mSocket.getInputStream());
			pw = new PrintWriter(mSocket.getOutputStream());
		} catch (IOException ioe) {
			Util.printExceptionToCommand(ioe);
			Util.printMessageToCommand(Constants.unableToGetStreams);
			return false;
		}
		return true;
	}
	
	public void sendMessage(String msg) {
		pw.println(msg);
		pw.flush();
	}
	
	public void run() {
		try {
			mFClientGUI.addMessage(Constants.waitingForFactoryConfigMessage);
			Factory factory;
			while(true) {
				// in case the server sends another factory to us
				Object obj = ois.readObject();
				if(obj instanceof Factory) {
					factory = (Factory)obj;
					mFManager.loadFactory(factory, mFClientGUI.getTable(), this);
					mFClientGUI.addMessage(Constants.factoryReceived);
					mFClientGUI.addMessage(factory.toString());
				} 
			}
		} catch (IOException ioe) {
			mFClientGUI.addMessage(Constants.serverCommunicationFailed);
		} catch (ClassNotFoundException cnfe) {
			Util.printExceptionToCommand(cnfe);
		}
	}

	public void addProductTosend(Product product) 
	{
		toSend.add(product);
	}
}
