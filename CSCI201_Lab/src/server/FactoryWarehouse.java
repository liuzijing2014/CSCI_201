package server;

import java.util.Random;
import java.util.Vector;

import resource.Factory;
import resource.Resource;

public class FactoryWarehouse implements Runnable
{
	private Vector<ServerClientCommunicator> communicators;
	private volatile Vector<Resource> resources;
	private Random rand;
	//private String need = null;
	
	FactoryWarehouse (Vector<ServerClientCommunicator> sccVector)
	{
		communicators = sccVector;
		rand = new Random();
		
	}
	
	public void setFactory(Factory factory)
	{
		resources = factory.getResources();
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		while(resources == null)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(true)
		{
			try
			{
				Thread.sleep(2000);
				
				int toStock = Math.abs(rand.nextInt() % resources.size());
				int number = Math.abs(rand.nextInt() % 25 + 1);
				for(ServerClientCommunicator communicator: communicators)
				{
					communicator.sendResource(new Resource(resources.elementAt(toStock).getName(), number));
					System.out.println("send need");
				}
				
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
