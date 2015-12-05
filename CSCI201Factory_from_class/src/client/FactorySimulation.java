package client;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import resource.Factory;
import resource.Resource;

public class FactorySimulation {
	private FactoryClientListener mFClientListener;
	
	Factory mFactory;
	
	private ArrayList<FactoryObject> mFObjects;
	private ArrayList<FactoryWorker> mFWorkers;
	private FactoryNode mFNodes[][];
	private Map<String, FactoryNode> mFNodeMap;
	private FactoryTaskBoard mTaskBoard;
	private FactoryMailbox mMailBox;
	private boolean isDone = false;
	private double totalTime = 0.0;
	
	//final
	private FactoryProductBin mFactoryProductBin;
	
	//For expanding Lock Lab
	//private Queue<FactoryWorker> line;
	
	//instance constructor
	{
		mFObjects = new ArrayList<FactoryObject>();
		mFWorkers = new ArrayList<FactoryWorker>();
		mFNodeMap = new HashMap<String, FactoryNode>();
		
		//For expanding Lock Lab
		//line = new LinkedBlockingQueue<FactoryWorker>();
	}
	
	//For expanding Lock Lab
	
	FactorySimulation(FactoryClientListener inClientListener, Factory inFactory, JTable inTable) {
		mFClientListener = inClientListener;
		
		mFactory = inFactory;
		mFNodes = new FactoryNode[mFactory.getWidth()][mFactory.getHeight()];
		
		//Create the nodes of the factory
		for(int height = 0; height < mFactory.getHeight(); height++) {
			for(int width = 0; width < mFactory.getWidth(); width++) {
				mFNodes[width][height] = new FactoryNode(width,height);
				mFObjects.add(mFNodes[width][height]);
			}
		}
		
		//Create the walls from a file
		Scanner reader = null;
		try {
			reader = new Scanner(new File("walls"));
			while(reader.hasNext())
			{
				int x = reader.nextInt();
				int y = reader.nextInt();
				String file = reader.next();
				FactoryWall fw = new FactoryWall(new Rectangle(x,y,1,1),file);
				mFObjects.add(fw);
				mFNodes[fw.getX()][fw.getY()].setObject(fw);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			if(reader != null) reader.close();
		}
		
		//Create the mailbox
		mMailBox = new FactoryMailbox(mFClientListener); //Mailbox that can stock the factory resources
		mFObjects.add(mMailBox); //Add this object to be rendered
		mFNodes[0][0].setObject(mMailBox); //Link this object to node 0,0
		mFNodeMap.put("MailBox", mFNodes[0][0]); //Make it easy to find the mailbox node
		
		//Create the workbenches
		Vector<FactoryWorkbench> workbenches = new Vector<FactoryWorkbench>();
		for(int i = 11; i < 13; ++i) {
			for(int j = 11; j < 13; ++j) {
				FactoryWorkbench wb = new FactoryWorkbench(new Rectangle(i, j, 1, 1));
				mFObjects.add(wb);
				mFNodes[i][j].setObject(wb);
				workbenches.add(wb);
			}
		}
		
		//Create the workroom door
		FactoryWorkroomDoor wDoor = new FactoryWorkroomDoor(new Rectangle(9, 12, 1, 1), workbenches);
		mFObjects.add(wDoor);
		mFNodes[9][12].setObject(wDoor);
		mFNodeMap.put("Workroom", mFNodes[9][12]);
		
		/*DEPRECATED EXPANDING ON THIS FROM SEMAPHORES LAB
		Create the resourceroom door
		FactoryResourceroomDoor rDoor = new FactoryResourceroomDoor(new Rectangle(6, 12, 1, 1));
		mFObjects.add(rDoor);
		mFNodes[6][12].setObject(rDoor);
		mFNodeMap.put("Resourceroom", mFNodes[6][12]);*/
		
		//Create the robot bin
		FactoryRobotBin robotBin = new FactoryRobotBin(new Rectangle(10, 0, 1, 1));
		mFObjects.add(robotBin);
		mFNodes[10][0].setObject(robotBin);
		mFNodeMap.put("RobotBin", mFNodes[10][0]);
		
		//Add an arbitrary amount of robots
		for(int i = 0; i < 20; ++i) {
			FactoryRobot fr = new FactoryRobot(i,this.getNode("RobotBin"),this);
			mFObjects.add(fr);
			mFWorkers.add(fr);
		}
		
		//Link all of the nodes together
		for(FactoryNode[] nodes: mFNodes) {
			for(FactoryNode node : nodes) {
				int x = node.getX();
				int y = node.getY();
				if(x != 0) node.addNeighbor(mFNodes[x-1][y]);
				if(x != mFactory.getWidth()-1) node.addNeighbor(mFNodes[x+1][y]);
				if(y != 0) node.addNeighbor(mFNodes[x][y-1]);
				if(y != mFactory.getHeight()-1) node.addNeighbor(mFNodes[x][y+1]);
			}
		}
		
		//Create the resources
		for(Resource resource : mFactory.getResources()) {
			FactoryResource fr = new FactoryResource(resource);
			mFObjects.add(fr);
			mFNodes[fr.getX()][fr.getY()].setObject(fr);
			mFNodeMap.put(fr.getName(), mFNodes[fr.getX()][fr.getY()]);
		}
		
		//Create the task board
		Point taskBoardLocation = mFactory.getTaskBoardLocation();
		mTaskBoard = new FactoryTaskBoard(inTable,inFactory.getProducts(),taskBoardLocation.x,taskBoardLocation.y);
		mFObjects.add(mTaskBoard);
		mFNodes[taskBoardLocation.x][taskBoardLocation.y].setObject(mTaskBoard);
		mFNodeMap.put("Task Board", mFNodes[taskBoardLocation.x][taskBoardLocation.y]);
		
		//Create the workers, set their first task to look at the task board
		for(int i = 0; i < mFactory.getNumberOfWorkers(); i++) {
			//Start each worker at the first node (upper left corner)
			//Note, may change this, but all factories have an upper left corner(0,0) so it makes sense
			FactoryWorker fw = new FactoryWorker(i, mFNodes[0][0], this);
			mFObjects.add(fw);
			mFWorkers.add(fw);
		}
		
//		//Create some stockpersons
//		for(int i = 0; i < 3; ++i) {
//			FactoryStockPerson sp = new FactoryStockPerson(i, mFNodes[0][0], this);
//			mFObjects.add(sp);
//			mFWorkers.add(sp);
//		}
		
		mFactoryProductBin = new FactoryProductBin(new Rectangle(14, 5, 1, 1));
		mFObjects.add(mFactoryProductBin);
		mFNodes[14][5].setObject(mFactoryProductBin);
		mFNodeMap.put("ProductBin", mFNodes[14][5]);
		
		
		//Start all the FactoryWorkers
		for(FactoryWorker fw : mFWorkers) fw.getThread().start();
	}
	
	public void update(double deltaTime) {
		totalTime += deltaTime;
		if(isDone)return;
		//Update all the objects in the factor that need updating each tick
		for(FactoryObject object : mFWorkers) object.update(deltaTime);
		if(mTaskBoard.isDone())
		{
			isDone = true;
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String outFileName = "reports/"+timestamp;
			outFileName = outFileName.replaceAll("[-:. ]", "_");
			File file = null;
			FileWriter fw = null;
			try{
				file = new File(outFileName);
				file.createNewFile();
				fw = new FileWriter(outFileName,true);
				for(FactoryObject object : mFObjects) {
					if(object instanceof FactoryReporter)
						((FactoryReporter) object).report(fw);
				}
			}catch(IOException e){
				System.out.println("Error occurred during file write:"+outFileName);
				if(file != null) file.delete();
			}finally {
				if(fw != null)
				try {
					fw.close();
				}catch(IOException e){
					System.out.println("Error: failed to close FileWriter!");
				}
			}
			
			DecimalFormat threePlaces = new DecimalFormat(".###");
			JOptionPane.showMessageDialog(null, "Total time: "+threePlaces.format(totalTime)+"s", 
					"Simulation Over!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void notifyServerProductMade(String productName) {
		mFClientListener.sendMessage("Made:" + productName);
	}
	
	public ArrayList<FactoryObject> getObjects() {
		return mFObjects;
	}
	
	public ArrayList<FactoryWorker> getWorkers() {
		return mFWorkers;
	}
	
	public FactoryNode[][] getNodes() {
		return mFNodes;
	}

	public String getName() {
		return mFactory.getName();
	}

	public double getWidth() {
		return mFactory.getWidth();
	}
	
	public double getHeight() {
		return mFactory.getHeight();
	}

	public FactoryNode getNode(String key) {
		return mFNodeMap.get(key);
	}

	public FactoryTaskBoard getTaskBoard() {
		return mTaskBoard;
	}
	
	public FactoryMailbox getMailBox() {
		return mMailBox;
	}
	
	public String getNeededResourceName() {
		String resourceName = "";
		HashMap<String, Integer> resourceCountMap = mFactory.getMaxResourceRequired();
		for(Resource resource : mFactory.getResources()) {
			if(resourceCountMap.containsKey(resource.getName())) {
				if(((FactoryResource) mFNodeMap.get(resource.getName()).getObject()).getAmount() < resourceCountMap.get(resource.getName())) {
					resourceName = resourceName+resource.getName()+"|";
				}
			}			
		}
		return resourceName;
	}
}
