package resource;

import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;

import utilities.Constants;

public class Factory implements Serializable {
	public static final long serialVersionUID = 1;
	private String name;
	private int numberOfWorkers;
	int width;
	int height;
	private Vector<Resource> resources;
	private Vector<Product> products;
	private Point taskBoardLocation;
	
	public Factory() {
		name = "";
		numberOfWorkers = 0;
		width = 0;
		height = 0;
		resources = new Vector<Resource>();
		products = new Vector<Product>();
	}
	
	public Factory(String name, int numberOfWorkers, int width, int height, Vector<Resource> resources, Vector<Product> products) {
		setName(name);
		setNumberOfWorkers(numberOfWorkers);
		setDimensions(width,height);
		setResources(resources);
		setProducts(products);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setNumberOfWorkers(int numberOfWorkers) {
		this.numberOfWorkers = numberOfWorkers;
	}
	
	public int getNumberOfWorkers() {
		return numberOfWorkers;
	}
	
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setResources(Vector<Resource> resources) {
		this.resources = new Vector<Resource>();
		for (Resource resource : resources) {
			addResource(resource);
		}
	}
	
	public void addResource(Resource resource) {
		if (resources == null) {
			resources = new Vector<Resource>();
		}
		resources.add(resource);	
	}

	public Vector<Resource> getResources() {
		return resources;
	}
	
	public void setProducts(Vector<Product> products) {
		this.products = new Vector<Product>();
		for (Product product : products) {
			addProduct(product);
		}
	}
	
	public void addProduct(Product product) {
		if (products == null) {
			products = new Vector<Product>();
		}
		products.add(product);
	}
	
	public Vector<Product> getProducts() {
		return products;
	}
	
	public void setTaskBoardLocation(Point taskBoardLocation) {
		this.taskBoardLocation = taskBoardLocation;
	}
	
	public Point getTaskBoardLocation() {
		return taskBoardLocation;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(Constants.factoryNameString + ": " + name);
		for (Resource resource : resources) {
			sb.append("\n");
			sb.append("\t" + resource);
		}
		for (Product product : products) {
			sb.append("\n");
			sb.append("\t" + product);
		}
		return sb.toString();
	}

}
