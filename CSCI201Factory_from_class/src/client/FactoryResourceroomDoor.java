package client;

import java.awt.Rectangle;
import java.util.concurrent.Semaphore;

import libraries.ImageLibrary;

public class FactoryResourceroomDoor extends FactoryObject {
	private Semaphore stockpersonPermit;
	private Semaphore workerPermit;

	public FactoryResourceroomDoor(Rectangle inDimensions) {
		super(inDimensions);
		stockpersonPermit = new Semaphore(1);
		workerPermit = new Semaphore(1);
		
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "door" + Constants.png);
		mLabel = "Resourceroom Door";
	}
	
	public void workerEnterResourceroom() throws InterruptedException {
		workerPermit.acquire();
	}
	
	public void workerLeaveResourceroom() {
		workerPermit.release();
	}
	
	public void stockpersonEnterResourceroom() throws InterruptedException {
		stockpersonPermit.acquire();
	}
	
	public void stockpersonLeaveResourceroom() {
		stockpersonPermit.release();
	}
}
