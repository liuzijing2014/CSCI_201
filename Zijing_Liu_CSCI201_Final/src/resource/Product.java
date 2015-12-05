package resource;

import java.io.Serializable;
import java.util.Vector;

import utilities.Constants;

public class Product implements Serializable {

	public static final long serialVersionUID = 1;
	private String name;
	private int quantity;
	private Vector<Resource> resourcesNeeded;
	
	public Product() {
		setName("");
		setQuantity(0);
		resourcesNeeded = new Vector<Resource>();
	}
	
	public Product(String name, int quantity, Vector<Resource> resourcesNeeded) {
		setName(name);
		setQuantity(quantity);
		setResourcesNeeded(resourcesNeeded);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setQuantity(int quantity) {
		if (quantity < 0) {
			this.quantity = 0;
		}
		this.quantity = quantity;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setResourcesNeeded(Vector<Resource> resourcesNeeded) {
		this.resourcesNeeded = new Vector<Resource>();
		for (Resource resource : resourcesNeeded) {
			addResourceNeeded(resource);
		}
	}
	
	public void addResourceNeeded(Resource resource) {
		if (resourcesNeeded == null) {
			resourcesNeeded = new Vector<Resource>();
		}
		resourcesNeeded.add(resource);
	}
	
	public Vector<Resource> getResourcesNeeded() {
		return resourcesNeeded;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(Constants.productString + ": " + name + " needs quantity " + quantity);
		for(Resource resource : resourcesNeeded) {
			sb.append("\n");
			sb.append("\t\t" + resource.toString());
		}
		return sb.toString();
	}

}
