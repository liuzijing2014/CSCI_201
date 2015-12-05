package client;

import java.awt.Rectangle;

import libraries.ImageLibrary;
import resource.Product;

public class FactoryMailbox extends FactoryObject {
//	private Vector<Resource> available;
//	private Queue<Resource> mail;
//	
	//final
	private FactoryClientListener mListener;
	
	protected FactoryMailbox(FactoryClientListener listener) {
		//Call the super constructor to ensure the mailbox is drawn correctly onto a node
		super(new Rectangle(0, 0, 1, 1));
//		available = deliveries;
//		mail = new LinkedList<Resource>();
		
		mListener = listener;
		
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "mailbox" + Constants.png);
		mLabel = "Mailbox";
	}
	
	
	public synchronized void sendProduct(Product product)
	{
		//mListener.addProductTosend(product);
		mListener.sendMessage("Ship:"+product.getName());
	}
}
