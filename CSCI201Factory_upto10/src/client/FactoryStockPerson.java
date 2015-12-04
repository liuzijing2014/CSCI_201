package client;

import libraries.ImageLibrary;
import resource.Resource;


public class FactoryStockPerson extends FactoryWorker {

	Resource mProductToStock;
	
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_empty" + Constants.png);
	}
	
	FactoryStockPerson(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(inNumber, startNode, inFactorySimulation);
		mLabel = "StockPerson "+inNumber;
	}

	@Override
	public void run() {
		mLock.lock();
		try {
			while(true) {
				if(mProductToStock == null) {
					mDestinationNode = mFactorySimulation.getNode("MailBox");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					while(!mDestinationNode.aquireNode())Thread.sleep(1);
					mProductToStock = mFactorySimulation.getMailBox().getStock();
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_box" + Constants.png);
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
				} else {
					mDestinationNode = mFactorySimulation.getNode(mProductToStock.getName());
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResource toGive = (FactoryResource)mDestinationNode.getObject();
					toGive.giveResource(mProductToStock.getQuantity());
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_empty" + Constants.png);
					mProductToStock = null;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}
	
}
