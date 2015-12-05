package client;

import libraries.ImageLibrary;
import resource.Resource;

//By extending FactoryWorker, we also implement Runnable
public class FactoryStockPerson extends FactoryWorker {
	private Resource mProductToStock;

	FactoryStockPerson(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		//In the FactoryWorker constructor, a new thread is created and the worker will
		// automatically start working
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
				//	mProductToStock = mFactorySimulation.getMailBox().getStock();
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockperson_box" + Constants.png);
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
				} else {
					
					/*DEPRECATED EXPANDING ON THIS FROM SEMAPHORES LAB
					Navigate to the resourceroom door and enter
					mDestinationNode = mFactorySimulation.getNode("Resourceroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResourceroomDoor rDoor = (FactoryResourceroomDoor)mDestinationNode.getObject();
					rDoor.stockpersonEnterResourceroom();*/
					
					//Drop off the resource
					mDestinationNode = mFactorySimulation.getNode(mProductToStock.getName());
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResource toGive = (FactoryResource)mDestinationNode.getObject();
					toGive.giveResource(mProductToStock.getQuantity());
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockperson_empty" + Constants.png);
					mProductToStock = null;
					
					/*DEPRECATED EXPANDING ON THIS FROM SEMAPHORES LAB
					Navigate back to the door and exit
					mDestinationNode = mFactorySimulation.getNode("Resourceroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					rDoor.stockpersonLeaveResourceroom();*/
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}
}
