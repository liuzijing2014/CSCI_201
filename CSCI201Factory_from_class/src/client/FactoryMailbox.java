package client;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import libraries.ImageLibrary;
import resource.Resource;

public class FactoryMailbox extends FactoryObject {
	private Vector<Resource> available;
	private Queue<Resource> mail;
	
	protected FactoryMailbox(Vector<Resource> deliveries) {
		//Call the super constructor to ensure the mailbox is drawn correctly onto a node
		super(new Rectangle(0, 0, 1, 1));
		available = deliveries;
		mail = new LinkedList<Resource>();
		
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "mailbox" + Constants.png);
		mLabel = "Mailbox";
	}
	
	public void insert(Resource resource) {
		for(Resource r : available) {
			if(r.getName().equals(resource.getName())) {
				mail.add(resource);
				break;
			}
		}
	}
	
	public Resource getStock() throws InterruptedException {
		while(mail.isEmpty()) {
			Thread.sleep(200);
		}
		return mail.remove();
	}
}
