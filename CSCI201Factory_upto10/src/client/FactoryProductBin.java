package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLException;

import libraries.ImageLibrary;
import resource.Product;

public class FactoryProductBin extends FactoryObject {
	
	private Vector<Product> finishedProducts;
	private Lock mLock;
	private Condition isEmpty;
	private Lock mLock2;

	protected FactoryProductBin(Rectangle inDimensions) {
		super(inDimensions);
		// TODO Auto-generated constructor stub
		
		finishedProducts = new Vector<Product>();
		mLock = new ReentrantLock();
		mLock2 = new ReentrantLock();
		isEmpty = mLock.newCondition();
		
		mLabel = "Product Bin";
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "wall" + Constants.png);
	}
	
	public void addProduct(Product product)
	{
		mLock2.lock();
		finishedProducts.add(product);
		//isEmpty.notify();
		mLock2.unlock();
	}
	
	public synchronized Product getProduct() throws InterruptedException
	{
		//mLock.lock();
		while(finishedProducts.isEmpty()) Thread.sleep(100);;
		//mLock.unlock();
		return finishedProducts.remove(0);
	}

	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
		g.setColor(Color.BLACK);
		g.drawString(finishedProducts.size()+"", centerTextX(g,finishedProducts.size()+""), centerTextY(g));
	}
}

