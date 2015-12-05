package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libraries.ImageLibrary;
import resource.Product;

public class FactoryProductBin extends FactoryObject {
	
	private Vector<Product> finishedProducts;
	private Lock mLock;
	private Condition isEmpty;

	protected FactoryProductBin(Rectangle inDimensions) {
		super(inDimensions);
		// TODO Auto-generated constructor stub
		
		finishedProducts = new Vector<Product>();
		mLock = new ReentrantLock();
		isEmpty = mLock.newCondition();
		
		mLabel = "Product Bin";
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "wall" + Constants.png);
	}
	
	public void addProduct(Product product)
	{
		mLock.lock();
		finishedProducts.add(product);
		//isEmpty.notify();
		mLock.unlock();
	}
	
	public Product getProduct() throws InterruptedException
	{
		mLock.lock();
		if(finishedProducts.isEmpty()) isEmpty.await();
		return finishedProducts.remove(0);
	}

	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
		g.setColor(Color.BLACK);
		g.drawString(finishedProducts.size()+"", centerTextX(g,finishedProducts.size()+""), centerTextY(g));
	}
}
