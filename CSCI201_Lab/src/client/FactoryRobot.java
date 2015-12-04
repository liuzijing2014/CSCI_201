package client;

import java.util.concurrent.locks.Condition;

import javax.swing.plaf.metal.MetalTreeUI;

import libraries.ImageLibrary;
import resource.Resource;

public class FactoryRobot extends FactoryWorker {

	private Resource mResource;
	private FactoryNode mReturnNode;
	
	private boolean workerHasArrived;
	private Condition shouldWait;
		
	FactoryRobot(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(inNumber, startNode, inFactorySimulation);
		// TODO Auto-generated constructor stub
		mLabel = "Robot" + inNumber;
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "robot" + Constants.png);
		
		workerHasArrived = false;
		shouldWait = mLock.newCondition();
	}
	
	public void run()
	{ 
		mLock.lock();
		try{
			if(mResource != null)
			{
				mDestinationNode = mFactorySimulation.getNode(mResource.getName());
				mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
				mNextNode = mShortestPath.pop();
				atLocation.await();
				FactoryResource toTake = (FactoryResource)mDestinationNode.getObject();
				toTake.takeResource(mResource.getQuantity());
				
				mDestinationNode = mReturnNode;
				mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
				mNextNode = mShortestPath.pop();
				atLocation.await();
				returnToBin();
			}
			else 
			{
				mDestinationNode = mFactorySimulation.getNode("RobotBin");
				mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
				mNextNode = mShortestPath.pop();
				atLocation.await();
				((FactoryRobotBin) mDestinationNode.getObject()).addRobot(this);
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}
	
	public void getResource(Resource resource, FactoryNode node)
	{
		mResource = resource;
		mReturnNode = node;
		mThread = new Thread(this);
		mThread.start();
	}

	private void returnToBin() throws InterruptedException
	{
		if(!workerHasArrived)shouldWait.await();
		mResource = null;
		mReturnNode = null;
		workerHasArrived = false;
		mThread = new Thread(this);
		mThread.start();
	}
	
	public void sendBack()
	{
		mLock.lock();
		workerHasArrived = true;
		shouldWait.signal();
		mLock.unlock();
	}
}
