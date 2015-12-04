package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import resource.Product;
import utilities.Util;
import libraries.ImageLibrary;

public class FactoryTaskBoard extends FactoryObject{
	
	//The table from the GUI to display
	private JTable mTable;
	private Vector<Vector<Object>> workerTableDataVector;
	private Vector<Object> workerTableColumnNames;
	
	//Products the workers must build
	private Queue<Product> mProducts;
	int totalProducts;
	int productsMade;
	
	//Lock for accessing the table
	Lock mLock;
	
	//instance constructor
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "taskboard" + Constants.png);
		mLabel = "Task Board";
		workerTableColumnNames = new Vector<Object>();
		workerTableDataVector = new Vector<Vector<Object>>();
		mProducts = new LinkedList<Product>();
		mLock = new ReentrantLock();
	}
	
	protected FactoryTaskBoard(JTable inTable, Vector<Product> inProducts, int x, int y) {
		super(new Rectangle(x,y,1,1));
		//Add the information to the task board
		mTable = inTable;
		for(Product product : inProducts) {
			for(int i = 0; i < product.getQuantity(); i++) {
				mProducts.add(product);
			}
		}
		for(int i=0; i < Constants.tableColumnNames.length; i++) {
			workerTableColumnNames.add(Constants.tableColumnNames[i]);
		}
		for (Product product : inProducts) {
			Vector<Object> productRow = new Vector<Object>(); 
			synchronized(this) {
				productRow.add(product.getName()); //Name of product
				productRow.add(product.getQuantity()); //How many to make
				productRow.add(0); //None in progress
				productRow.add(0); //None completed
				workerTableDataVector.add(productRow);
				updateWorkerTable();
			}
		}
		totalProducts = mProducts.size();
		productsMade = 0;
	}
	
	public Product getTask() {
		mLock.lock();
		Product toAssign = null;
		if(mProducts.isEmpty())  {
			mLock.unlock();
			return null;
		}
		
		toAssign = mProducts.remove();
		for (Vector<Object> vect : workerTableDataVector) {
			String name = (String)vect.get(Constants.productNameIndex);
			if (name.equals(toAssign.getName())) {
				vect.setElementAt((int)vect.get(Constants.startedIndex)+1, Constants.startedIndex);
				break;
			}
		}
		mTable.revalidate();
		mTable.repaint();
		mLock.unlock();
		return toAssign;
	}
	
	public void endTask(Product productMade) {
		mLock.lock();
		for (Vector<Object> vect : workerTableDataVector) {
			String name = (String)vect.get(Constants.productNameIndex);
			if (name.equals(productMade.getName())) {
					vect.setElementAt((int)vect.get(Constants.startedIndex)-1, Constants.startedIndex);
					vect.setElementAt((int)vect.get(Constants.completedIndex)+1, Constants.completedIndex);
				break;
			}
		}
		mTable.revalidate();
		mTable.repaint();
		productsMade++;
		mLock.unlock();
	}
	
	private synchronized void updateWorkerTable() {
		mLock.lock();
		try {
			SwingUtilities.invokeLater(new Runnable(){public void run(){
			((DefaultTableModel)mTable.getModel()).setDataVector(workerTableDataVector, workerTableColumnNames);
			}});
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			Util.printExceptionToCommand(aioobe);
		}
		mLock.unlock();
	}

	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
	}

	public boolean isDone() {
		return (productsMade == totalProducts);
	}

}
