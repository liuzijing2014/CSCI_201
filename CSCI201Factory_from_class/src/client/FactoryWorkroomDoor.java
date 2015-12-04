package client;

import java.awt.Rectangle;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import libraries.ImageLibrary;

public class FactoryWorkroomDoor extends FactoryObject {
	private Semaphore workPermits;
	private Vector<FactoryWorkbench> availableWorkbenches;

	public FactoryWorkroomDoor(Rectangle inDimensions, Vector<FactoryWorkbench> workbenches) {
		super(inDimensions);
		availableWorkbenches = workbenches;
		workPermits = new Semaphore(availableWorkbenches.size());
		
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "door" + Constants.png);
		mLabel = "Workroom Door";
	}
	
	public FactoryWorkbench getWorkbench() throws InterruptedException {
		workPermits.acquire();
		return availableWorkbenches.remove(0);
	}
	
	public void returnWorkbench(FactoryWorkbench wb) {
		availableWorkbenches.add(wb);
		workPermits.release();
	}
}
