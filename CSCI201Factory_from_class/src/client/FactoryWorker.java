package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libraries.ImageLibrary;
import resource.Product;
import resource.Resource;

public class FactoryWorker extends FactoryObject implements Runnable, FactoryReporter{
	
	private int mNumber;
	
	private Timestamp finished;
	
	protected FactorySimulation mFactorySimulation;
	protected Product mProductToMake;
	
	protected Lock mLock;
	protected Condition atLocation;
	
	protected Thread mThread;
	
	//Nodes each worker keeps track of for path finding
	protected FactoryNode mCurrentNode;
	protected FactoryNode mNextNode;
	protected FactoryNode mDestinationNode;
	protected Stack<FactoryNode> mShortestPath;
	
	//instance constructor
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "worker" + Constants.png);
		mLock = new ReentrantLock();
		atLocation = mLock.newCondition();
	}
	
	FactoryWorker(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(new Rectangle(startNode.getX(), startNode.getY(), 1, 1));
		mNumber = inNumber;
		mCurrentNode = startNode;
		mFactorySimulation = inFactorySimulation;
		mLabel = Constants.workerString + String.valueOf(mNumber);
		mThread = new Thread(this);
	}
	
	@Override
	public void report(FileWriter fw) throws IOException {
		fw.write(mNumber+" finished at "+finished+'\n');		
	}
	
	public Thread getThread() {
		return mThread;
	}
	
	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
		g.fillOval(renderBounds.x + renderBounds.width / 3, renderBounds.y + renderBounds.height / 3, renderBounds.width / 10, renderBounds.height / 7);
		g.fillOval(renderBounds.x + renderBounds.width / 2, renderBounds.y + renderBounds.height / 3, renderBounds.width / 10, renderBounds.height / 7);
	}
	
	@Override
	public void update(double deltaTime) {
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
				if(mCurrentNode == mDestinationNode) {
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
			//Thread.sleep(1000*mNumber); //used to space out the factory workers
			while(true) {
				if(mProductToMake == null) {
					/*ATTEMPTED EXPANDING ON THIS FROM LOCKS AND MONITORS LAB
					//get an assignment from the table
					FactoryNode taskboardNode = mFactorySimulation.getNode("Task Board");
					if(mCurrentNode != taskboardNode) {
						mShortestPath = mCurrentNode.findShortestPath(taskboardNode);
						mShortestPath.remove(taskboardNode);
						mDestinationNode = mShortestPath.firstElement();
						mNextNode = mShortestPath.pop();
						atLocation.await();						
					}
					mFactorySimulation.getTaskBoard().lockQueue();
					mDestinationNode = taskboardNode;
					mNextNode = taskboardNode;
					atLocation.await();
					mFactorySimulation.getTaskBoard().inspectTasks();
					mProductToMake = mFactorySimulation.getTaskBoard().getTask();
					mFactorySimulation.getTaskBoard().unlockQueue();
					if(mProductToMake == null) break; //No more tasks, end here;*/
					
					
										
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
				
				{
					/*DEPRECATED EXPANDING ON THIS FROM SEMAPHORES LAB
					Navigate to the resourceroom door and enter
					mDestinationNode = mFactorySimulation.getNode("Resourceroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResourceroomDoor rDoor = (FactoryResourceroomDoor)mDestinationNode.getObject();
					rDoor.workerEnterResourceroom();*/
				}
				
				ArrayList<FactoryRobot> robots = new ArrayList<FactoryRobot>();
				//We want to keep a separate list of Threads so that we can check if the robot has finished its task
				ArrayList<Thread> robotThreads = new ArrayList<Thread>();
				FactoryRobotBin robotBin;
				//Go to the robot bin
				{
					mDestinationNode = mFactorySimulation.getNode("RobotBin");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					robotBin = (FactoryRobotBin) mDestinationNode.getObject();
				}
				for(Resource resource : mProductToMake.getResourcesNeeded()) {
					//Attempt to grab a robot for each resource to collect
					FactoryRobot robot = robotBin.getRobot();
					if(robot != null) {
						//Successful, send the robot off
						robots.add(robot);
						robot.getResource(resource, mFactorySimulation.getNode("Workroom"));
						robotThreads.add(robot.getThread());
					} else {
						//No robots available, go get the resource
						mDestinationNode = mFactorySimulation.getNode(resource.getName());
						mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
						mNextNode = mShortestPath.pop();
						atLocation.await();
						FactoryResource toTake = (FactoryResource)mDestinationNode.getObject();
						toTake.takeResource(resource.getQuantity());
						if(!mProductToMake.getResourcesNeeded().lastElement().equals(resource)) {
							//Go back to the FactoryRobotBin to see if more robots are available
							mDestinationNode = mFactorySimulation.getNode("RobotBin");
							mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
							mNextNode = mShortestPath.pop();
							atLocation.await();
						}
					}
					
				}
				
				{
					/*DEPRECATED EXPANDING ON THIS FROM SEMAPHORES LAB
					Navigate back to the door and exit
					mDestinationNode = mFactorySimulation.getNode("Resourceroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					rDoor.workerLeaveResourceroom();*/
				}
				
				//build the product
				{
					//Navigate to the workroom door
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					//Signal robots to let them know of arrival
					for(FactoryRobot r : robots) r.sendBack();
					//Wait for robots to return with materials
					for(Thread t : robotThreads) t.join();
					//Get an available workbench, and navigate to it
					FactoryWorkroomDoor door = (FactoryWorkroomDoor)mDestinationNode.getObject();
					FactoryWorkbench workbench = door.getWorkbench();
					mDestinationNode = mFactorySimulation.getNodes()[workbench.getX()][workbench.getY()];
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					//Create the product
					workbench.assemble(mProductToMake);
					//Navigate back to the door to exit
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					//Give up permit since we are exiting
					door.returnWorkbench(workbench);
				}
				//update table
				{
					/*ATTEMPTED EXPANDING ON THIS FROM LOCKS AND MONITORS LAB
					FactoryNode taskboardNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(taskboardNode);
					mShortestPath.remove(taskboardNode);
					mDestinationNode = mShortestPath.firstElement();
					mNextNode = mShortestPath.pop();
					atLocation.await();
					mFactorySimulation.getTaskBoard().lockQueue();
					mDestinationNode = taskboardNode;
					mNextNode = taskboardNode;
					atLocation.await();
					mFactorySimulation.notifyServerProductMade(mProductToMake.getName());
					finished = new Timestamp(System.currentTimeMillis());
					mFactorySimulation.getTaskBoard().endTask(mProductToMake);
					mFactorySimulation.getTaskBoard().unlockQueue();
					mProductToMake = null;*/
					
					
					
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					//mFactorySimulation.notifyServerProductMade(mProductToMake.getName());
					finished = new Timestamp(System.currentTimeMillis());
					mFactorySimulation.getTaskBoard().endTask(mProductToMake);
					//mProductToMake = null;
					
					//final 
					mDestinationNode = mFactorySimulation.getNode("ProductBin");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryProductBin mFactoryProductBin = ((FactoryProductBin)mDestinationNode.getObject());
					mFactoryProductBin.addProduct(mProductToMake);
					mProductToMake = null;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}
}
