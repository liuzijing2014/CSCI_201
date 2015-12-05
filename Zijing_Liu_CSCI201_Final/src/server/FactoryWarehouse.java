package server;

import java.util.Random;
import java.util.Vector;

import resource.Factory;
import resource.Resource;

public class FactoryWarehouse implements Runnable{

	private volatile Vector<Resource> resources;
	private Vector<ServerClientCommunicator> communicators;
	
	Random rand = new Random();
	
	FactoryWarehouse(Vector<ServerClientCommunicator> sccVector) {
		communicators = sccVector;
	}
	
	public void setFactory(Factory factory) {
		resources = factory.getResources();
	}
	
	@Override
	public void run() {
		while(resources == null)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {e1.printStackTrace();}
		while(true) {
			try {
				Thread.sleep(7500);
				int toStock = Math.abs(rand.nextInt() % resources.size());
				int number = Math.abs(rand.nextInt() % 25 + 1);
				for(ServerClientCommunicator communicator : communicators) {
					communicator.sendResource(new Resource(resources.elementAt(toStock).getName(),number));
				}
			} catch (InterruptedException e) {e.printStackTrace();
			}
		}
	}

}
