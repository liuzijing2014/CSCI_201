package client;

import libraries.ImageLibrary;
import resource.Product;

public class FactoryShipper extends FactoryWorker {
	
	Product mProductToStock;
	
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockperson_empty" + Constants.png);
	}

	FactoryShipper(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(inNumber, startNode, inFactorySimulation);
		// TODO Auto-generated constructor stub
		
		mLabel = "Factory Shipper" + inNumber;
	}
	
	@Override
	public void run() {
		mLock.lock();
		try {
			while(true) {
				if(mProductToStock == null) {
					mDestinationNode = mFactorySimulation.getNode("ProductBin");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryProductBin mProductBin = (FactoryProductBin) mDestinationNode.getObject();
					mProductToStock = mProductBin.getProduct();
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockperson_box" + Constants.png);
				} else {
					mDestinationNode = mFactorySimulation.getNode("MailBox");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryMailbox mailbox = (FactoryMailbox) mDestinationNode.getObject();
					mailbox.sendProduct(mProductToStock);
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockperson_empty" + Constants.png);
					
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					// do some stuff
					FactoryTaskBoard taskBoard = (FactoryTaskBoard) mDestinationNode.getObject();
					taskBoard.endShipped(mProductToStock);

					mProductToStock = null;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}

}
