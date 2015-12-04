package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libraries.ImageLibrary;
import resource.Resource;

public class FactoryResource extends FactoryObject implements FactoryReporter{
	private Lock mLock;
	private Condition isEmpty;
	
	private final Resource mResource;
	int startAmount;
	
	FactoryResource(Resource inResource) {
		super(new Rectangle(inResource.getX(),inResource.getY(),1,1));
		mResource = inResource;
		mLabel = inResource.getName();
		mImage = ImageLibrary.getImage(Constants.resourceFolder + inResource.getName() + Constants.png);
		startAmount = mResource.getQuantity();
		mLock = new ReentrantLock();
		isEmpty = mLock.newCondition();
	}

	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
		g.setColor(Color.BLACK);
		g.drawString(mResource.getQuantity()+"", centerTextX(g,mResource.getQuantity()+""), centerTextY(g));
	}
	
	public void takeResource(int amount) throws InterruptedException {
		mLock.lock();
		while(amount > mResource.getQuantity()) isEmpty.await();
		mResource.deductFromQuantity(amount);
		mLock.unlock();
	}
	
	public void giveResource(int amount) {
		mLock.lock();
		mResource.addToQuantity(amount);
		isEmpty.signalAll();
		mLock.unlock();
	}

	public int getX() {
		return mResource.getX();
	}
	public int getY() {
		return mResource.getY();
	}

	public String getName() {
		return mResource.getName();
	}
	
	public int getAmount() {
		return mResource.getQuantity();
	}

	@Override
	public void report(FileWriter fw) throws IOException {
		fw.write("Total resources: "+mResource.getQuantity()+"/"+startAmount
				+"Taken: "+(startAmount-mResource.getQuantity())+'\n');		
	}
}
