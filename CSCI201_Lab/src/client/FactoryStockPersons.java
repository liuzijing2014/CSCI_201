package client;

import libraries.ImageLibrary;
import resource.Resource;

public class FactoryStockPersons extends FactoryWorker {
	
	private Resource mProductToStock = null;

	FactoryStockPersons(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(inNumber, startNode, inFactorySimulation);
		// TODO Auto-generated constructor stub
		mLabel = "StockPerson" + inNumber;
	}
	
	public void run()
	{
		mLock.lock();
		try
		{
			while(true)
			{
				if(mProductToStock == null)
				{
					mDestinationNode = mFactorySimulation.getNode("MailBox");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					try {
						atLocation.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					while(!mDestinationNode.aquireNode())Thread.sleep(1);
					mProductToStock = mFactorySimulation.getMailBox().getStock();
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
				}
				else
				{
					mDestinationNode = mFactorySimulation.getNode(mProductToStock.getName());
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_box" + Constants.png);
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResource toGive = (FactoryResource)mDestinationNode.getObject();
					toGive.takeResource(-mProductToStock.getQuantity());
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_empty" + Constants.png);
					mProductToStock = null;
				}
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		mLock.unlock();
	}
		
}
