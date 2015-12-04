package server;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import resource.Factory;
import resource.Resource;

public class FactoryWarehouse implements Runnable {
	private Vector<ServerClientCommunicator> communicators;
	private volatile Vector<Resource> resources;
	private Queue<String> requests;
	
	FactoryWarehouse(Vector<ServerClientCommunicator> sccVector) {
		communicators = sccVector;
		requests = new LinkedList<String>();
	}
	
	public void setFactory(Factory factory) {
		resources = factory.getResources();
	}

	@Override
	public void run() {
		Random rand = new Random();
		while(resources == null) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(true) {
			try {
				Thread.sleep(1500);
				String name = null;
				int number = Math.abs(rand.nextInt() % 25 + 1);
				while(!requests.isEmpty()) {
					if(isValidResource(requests.peek())) {
						name = requests.remove();
						break;
					}
					else {
						requests.remove();
					}
				}
				if(name == null) {
					int index = Math.abs(rand.nextInt() % resources.size());
					name = resources.elementAt(index).getName();
				}
				for(ServerClientCommunicator communicator : communicators) {
					communicator.sendResource(new Resource(name, number));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void requestResource(String resourceNames) {
		String[] names = resourceNames.split("\\|");
		for(String s : names) {
			if(isValidResource(s)) {
				requests.add(s);
			}
		}
	}
	
	private boolean isValidResource(String name) {
		for(Resource resource : resources) {
			if(resource.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
