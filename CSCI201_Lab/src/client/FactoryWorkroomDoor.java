package client;

import java.awt.Rectangle;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;

import libraries.ImageLibrary;

public class FactoryWorkroomDoor extends FactoryObject {

	private Semaphore workPermits;
	private Vector<FactoryWorkbench> availableWorkbenches;
	
	protected FactoryWorkroomDoor(Rectangle inDimensions, Vector<FactoryWorkbench> workbenchs) {
		super(inDimensions);
		// TODO Auto-generated constructor stub
		availableWorkbenches = workbenchs;
		workPermits = new Semaphore(availableWorkbenches.size());
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "door" + Constants.png);
		mLabel = "Workroom Door";
	}
	
	public FactoryWorkbench getWorkbench() throws InterruptedException{
		workPermits.acquire();
		return availableWorkbenches.remove(0);
	}
	
	public void returnWorkbench(FactoryWorkbench wb){
		availableWorkbenches.add(wb);
		workPermits.release();
	}

}
