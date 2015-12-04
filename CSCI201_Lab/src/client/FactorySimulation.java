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
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import resource.Factory;
import resource.Resource;

public class FactorySimulation {
	
	Factory mFactory;
	
	
	private ArrayList<FactoryObject> mFObjects;
	private ArrayList<FactoryWorker> mFWorkers;
	private FactoryNode mFNodes[][];
	private Map<String, FactoryNode> mFNodeMap;
	private FactoryTaskBoard mTaskBoard;
	private boolean isDone;
	private double totalTime = 0.0;
	private FactoryMailBox mMailBox;
	private FactoryManager mFactoryManager;
	private Vector<FactoryWorkbench> mFactoryworkbenches;
	//private FactoryWorkbench mFactoryWorkbench;
	//private LinkedList<FactoryWorker> waitingList;
	
	//instance constructor
	{
		mFObjects = new ArrayList<FactoryObject>();
		mFWorkers = new ArrayList<FactoryWorker>();
		mFNodeMap = new HashMap<String, FactoryNode>();
		mFactoryworkbenches = new Vector<FactoryWorkbench>();
		//waitingList = new LinkedList<FactoryWorker>();
	}
	
//	public LinkedList<FactoryWorker> getWaitingList() {
//		return waitingList;
//	}
//
//	public void putToWait(FactoryWorker worker)
//	{
//		waitingList.addLast(worker);
//	}
	
//	public void quitWaiting(FactoryWorker worker)
//	{
//		waitingList.remove(worker);
//	}
	
	FactorySimulation(Factory inFactory, JTable inTable, FactoryManager mFactoryManager) {
		isDone = false;
		mFactory = inFactory;
		this.mFactoryManager = mFactoryManager;
		mFNodes = new FactoryNode[mFactory.getWidth()][mFactory.getHeight()];
			
		//Create the nodes of the factory
		for(int height = 0; height < mFactory.getHeight(); height++) {
			for(int width = 0; width < mFactory.getWidth(); width++) {
				mFNodes[width][height] = new FactoryNode(width,height);
				mFObjects.add(mFNodes[width][height]);
			}
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
		
		for(int i = 11; i < 13; ++i)
		{
			for(int j = 11; j < 13; ++j)
			{
				FactoryWorkbench mFactoryWorkbench = new FactoryWorkbench(new Rectangle(i, j, 1, 1));
				mFNodes[i][j].setObject(mFactoryWorkbench);
				//mFNodeMap.put("Workbench", mFNodes[0][mFactory.getHeight()-1]);
				mFObjects.add(mFactoryWorkbench);				
				mFactoryworkbenches.add(mFactoryWorkbench);
			}
		}
		
		FactoryWorkroomDoor door = new FactoryWorkroomDoor(new Rectangle(9, 12, 1, 1), mFactoryworkbenches);
		mFNodes[9][12].setObject(door);
		mFNodeMap.put("Workroom", mFNodes[9][12]);
		mFObjects.add(door);
		
//		mFactoryWorkbench = new FactoryWorkbench(new Rectangle(0, mFactory.getHeight()-1, 1, 1));
//		mFNodes[0][mFactory.getHeight()-1].setObject(mFactoryWorkbench);
//		mFNodeMap.put("Workbench", mFNodes[0][mFactory.getHeight()-1]);
//		mFObjects.add(mFactoryWorkbench);
		
		
		
		Scanner reader = null;
		try
		{
			reader = new Scanner(new File("walss"));
			while(reader.hasNext())
			{
				int x = reader.nextInt();
				int y = reader.nextInt();
				String file = reader.next();
				FactoryWall fw = new FactoryWall(new Rectangle(x, y, 1, 1), file);
				mFObjects.add(fw);
				mFNodes[fw.getX()][fw.getY()].setObject(fw);
			}
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				reader.close();
			}
		}
		
		
		
		//Create the workers, set their first task to look at the task board
		for(int i = 0; i < mFactory.getNumberOfWorkers()+5; i++) {
			//Start each worker at the first node (upper left corner)
			//Note, may change this, but all factories have an upper left corner(0,0) so it makes sense
			FactoryWorker fw = new FactoryWorker(i, mFNodes[0][0], this);
			mFObjects.add(fw);
			mFWorkers.add(fw);
		}
		
		for(int i = 0; i < 3; ++i)
		{
			FactoryStockPersons sp = new FactoryStockPersons(i, mFNodes[0][0], this);
			mFObjects.add(sp);
			mFWorkers.add(sp);
		}
		
		mMailBox = new FactoryMailBox(mFactory.getResources());
		mFObjects.add(mMailBox);
		mFNodes[0][0].setObject(mMailBox);
		mFNodeMap.put("MailBox", mFNodes[0][0]);
		
		FactoryRobotBin robotBin = new FactoryRobotBin(new Rectangle(10, 0, 1, 1));
		mFNodes[10][0].setObject(robotBin);
		mFNodeMap.put("RobotBin", mFNodes[10][0	]);
		mFObjects.add(robotBin);
		
		for(int i = 0; i < 15; i++)
		{
			FactoryRobot robot = new FactoryRobot(i, this.getNode("RobotBin"), this);
			mFObjects.add(robot);
			mFWorkers.add(robot);
		}
		
		for(FactoryWorker fw: mFWorkers)
		{
			fw.getThread().start();
		}
	}
	
	public void update(double deltaTime) {
		
		totalTime += deltaTime;
		if(isDone)
		{
			return;
		}
		//Update all the objects in the factor that need updating each tick
		for(FactoryObject object : mFWorkers) object.update(deltaTime);
		
		if(mTaskBoard.isDone())
		{
			isDone = true;
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String outFileName = "reports/" + timestamp;
			outFileName = outFileName.replaceAll("[-:.]", "_");
			File file = null;
			FileWriter fw = null;
			try 
			{
				file = new File(outFileName);
				file.createNewFile();
				fw = new FileWriter(outFileName, true);
				for(FactoryObject object : mFObjects)
				{
					if(object instanceof FactoryReporter)
					{
						((FactoryReporter)object).report(fw);
					}
				}
			}
			catch (IOException e) 
			{
				System.out.println("Error occured during file write: " + outFileName);
				if(file != null) file.delete();
			}
			finally
			{
				if(fw != null)
				{
					try
					{
						fw.close();
					}
					catch(IOException e)
					{
						System.out.println("Error: failed to close FileWriter");
					}
				}
		
				DecimalFormat threePlaces = new DecimalFormat(".###");
				JOptionPane.showMessageDialog(null, "Total time: " + threePlaces.format(totalTime)+ "s", "Simulation Over!", JOptionPane.INFORMATION_MESSAGE);
			}
		}
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
	
	public FactoryMailBox getMailBox()
	{
		return mMailBox;	
	}
	public FactoryManager getFactoryManager()
	{
		return mFactoryManager;
	}
	
}
