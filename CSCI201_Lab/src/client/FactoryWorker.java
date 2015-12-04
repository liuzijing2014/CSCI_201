package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libraries.ImageLibrary;
import resource.Product;
import resource.Resource;

public class FactoryWorker extends FactoryObject implements Runnable, FactoryReporter{
	
	private int mNumber;
	
	protected FactorySimulation mFactorySimulation;
	private Product mProductToMake;
	
	protected Lock mLock;
	protected Condition atLocation;
	
	//Nodes each worker keeps track of for path finding
	protected FactoryNode mCurrentNode;
	protected FactoryNode mNextNode;
	protected FactoryNode mDestinationNode;
	protected Stack<FactoryNode> mShortestPath;
	
	private LinkedList<FactoryWorker> ahead;
	private boolean wait;
	
	private Timestamp finished;
	
	protected Thread mThread;
	
	//instance constructor
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "worker" + Constants.png);
		mLock = new ReentrantLock();
		atLocation = mLock.newCondition();
		ahead = new LinkedList<FactoryWorker>();
		wait = false;
	}
	
	FactoryWorker(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(new Rectangle(startNode.getX(), startNode.getY(), 1, 1));
		mNumber = inNumber;
		mCurrentNode = startNode;
		mFactorySimulation = inFactorySimulation;
		mLabel = Constants.workerString + String.valueOf(mNumber);
		mThread = new Thread(this);
	}
	
	public Thread getThread()
	{
		return mThread;
	}
	
	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
	}
	
	@Override
	public void update(double deltaTime){
		if(!mLock.tryLock()) return;
		//if we have somewhere to go, go there
		if(mDestinationNode != null) {
			if(moveTowards(mNextNode,deltaTime * Constants.workerSpeed)) {
				//if we arrived, save our current node
				mCurrentNode = mNextNode;
				if(!mShortestPath.isEmpty()) {
					//if we have somewhere else to go, save that location
					mNextNode = mShortestPath.pop();
					mCurrentNode.unMark();
				}//if we arrived at the location, signal the worker thread so they can do more actions
				if(mCurrentNode == mDestinationNode) 
				{
					mDestinationNode.unMark();
					atLocation.signal();
				}
			}
		}
		mLock.unlock();
	}
	
	//Use a separate thread for expensive operations
	//Path finding
	//Making objects
	//Waiting
	@Override
	public void run() {
		mLock.lock();
		try {
			while(true) {
				if(mProductToMake == null) {
					//get an assignment from the table
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					while(!mDestinationNode.aquireNode())Thread.sleep(1);
					mProductToMake = mFactorySimulation.getTaskBoard().getTask();
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
					if(mProductToMake == null) break; //No more tasks, end here
				}
				
				ArrayList<FactoryRobot> robots = new ArrayList<FactoryRobot>();
				ArrayList<Thread> robotThreads = new ArrayList<Thread>();
				FactoryRobotBin robotBin;
				
				mDestinationNode = mFactorySimulation.getNode("RobotBin");
				mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
				mNextNode = mShortestPath.pop();
				atLocation.await();
				robotBin = (FactoryRobotBin)mDestinationNode.getObject();
				//build the product
				for(Resource resource : mProductToMake.getResourcesNeeded()) {
					FactoryRobot robot = robotBin.getRobot();
					if(robot != null)
					{
						robots.add(robot);
						robot.getResource(resource, mFactorySimulation.getNode("Workroom"));
						robotThreads.add(robot.getThread());
					}
					else {						
						mDestinationNode = mFactorySimulation.getNode(resource.getName());
						mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
						mNextNode = mShortestPath.pop();
						atLocation.await();
						if(!mProductToMake.getResourcesNeeded().lastElement().equals(resource))
						{
							mDestinationNode = mFactorySimulation.getNode("RobotBin");
							mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
							mNextNode = mShortestPath.pop();
							atLocation.await();
						}
//						FactoryResource toTake = (FactoryResource)mDestinationNode.getObject();
//						toTake.takeResource(resource.getQuantity());
					}
				}
				
				//make product in the workbench
				{
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					
					for(FactoryRobot r: robots) r.sendBack();
					for(Thread t: robotThreads) t.join();
					
					FactoryWorkroomDoor door = (FactoryWorkroomDoor) mDestinationNode.getObject();
					FactoryWorkbench workbench = door.getWorkbench();
					mDestinationNode = mFactorySimulation.getNodes()[workbench.getX()][workbench.getY()];
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					
					workbench.assemble(mProductToMake);
					
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					door.returnWorkbench(workbench);
				}
				
				//update table
				{
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					finished = new Timestamp(System.currentTimeMillis());
					mFactorySimulation.getTaskBoard().endTask(mProductToMake);
					
					//Notify Factory an item has been made 
					{
						mFactorySimulation.getFactoryManager().finishItem(mProductToMake.getName());
					}
					
					mProductToMake = null;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}
	
	public void report(FileWriter fw) throws IOException
	{
		fw.write(mNumber + " finished at " + finished + "\n");
	}

}
