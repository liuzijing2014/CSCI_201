package server;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JComboBox;

import resource.Factory;
import resource.Product;
import resource.Resource;
import utilities.Util;

public class ConfigureFactoryListener implements ActionListener {
	
	private JComboBox<String> factoryFileComboBox;
	private Factory factory;
	
	public ConfigureFactoryListener(JComboBox<String> factoryFileComboBox) {
		this.factoryFileComboBox = factoryFileComboBox;
	}
	
	public void actionPerformed(ActionEvent ae) {
		BufferedReader br = null;
		try {
			String factoryFileName = (String)factoryFileComboBox.getSelectedItem();
			br = new BufferedReader(new FileReader(Constants.defaultResourcesDirectory + factoryFileName));
			factory = readFile(br);
			
			// once a new factory is loaded on the server, send the new factory to all of the clients that are connected
			FactoryServer.sendFactory(factory);
		} catch (FileNotFoundException fnfe) {
			Util.printExceptionToCommand(fnfe);
		} catch (IOException ioe) {
			Util.printExceptionToCommand(ioe);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe1) {
					Util.printExceptionToCommand(ioe1);
				}
			}
		}
	}
	
	public Factory getFactory() {
		return factory;
	}
	
	private Factory readFile(BufferedReader br) throws IOException {
		factory = new Factory();
		String line = br.readLine();
		while (line != null) {
			line = line.trim();
			if (line.startsWith("--")) {
				// ignore line since it is a comment
			}
			else if (line.startsWith(utilities.Constants.resourceString)) {
				Resource resource = parseResource(line);
				factory.addResource(resource);
			}
			else if (line.startsWith(utilities.Constants.productString)) {
				Product product = parseProduct(line);
				factory.addProduct(product);
			}
			else if (line.startsWith(utilities.Constants.factoryNameString)) {
				parseFactory(factory, line);
			}
			else if (line.startsWith(utilities.Constants.taskboardString)) {
				parseTaskBoardLocation(factory, line);
			}
			else if (line.length() == 0) {
				// this would be a blank line
			}
			else {
				// this would be an unrecognized line
				Util.printMessageToCommand(Constants.unrecognizedLine + line);
			}
			line = br.readLine();
		}
		FactoryServerGUI.addMessage(factory.toString());
		FactoryServerGUI.addMessage(Constants.factoryLoadedMessage);
		return factory;
	}

	private Resource parseResource(String line) {
		StringTokenizer st = new StringTokenizer(line, Constants.factoryFileDelimeter);
		st.nextToken(); // description, which should be "Resource"
		String name = st.nextToken();
		int quantity = 0;
		try {
			quantity = Integer.parseInt(st.nextToken());
			if (quantity < 0) {
				quantity = 0;
			}
		} catch (Exception ex) {
			// if the quantity can't be made into an integer, we will use 0 as the quantity
			Util.printExceptionToCommand(ex);
		}
		int x = 0;
		int y = 0;
		try {
			x = Integer.parseInt(st.nextToken());
			if (x < 0) {
				x = 0;
			}
			y = Integer.parseInt(st.nextToken());
			if (y < 0) {
				y = 0;
			}
		} catch (Exception ex) {
			// if the quantity can't be made into an integer, we will use 0 as the quantity
			Util.printExceptionToCommand(ex);
		}
		Resource resource = new Resource(name, quantity, x, y);
		return resource;
	}
	
	private Product parseProduct(String line) {
		StringTokenizer st = new StringTokenizer(line, Constants.factoryFileDelimeter);
		st.nextToken(); // description, which should be "Product"
		String name = st.nextToken();
		Product product = new Product();
		product.setName(name);
		int productQuantity = 0;
		try {
			productQuantity = Integer.parseInt(st.nextToken());
			if (productQuantity < 0) {
				productQuantity = 0;
			}
		} catch (Exception ex) {
			// if the productQuantity can't be made into an integer, we will use 0 as the quantity
			Util.printExceptionToCommand(ex);
		}
		product.setQuantity(productQuantity);
		while(st.hasMoreElements()) {
			String resourceName = st.nextToken();
			int quantity = 0;
			try {
				quantity = Integer.parseInt(st.nextToken());
				if (quantity < 0) {
					quantity = 0;
				}
			} catch (Exception ex) {
				// if the quantity can't be made into an integer, we will use 0 as the quantity
				Util.printExceptionToCommand(ex);
			}
			product.addResourceNeeded(new Resource(resourceName, quantity));
		}
		return product;
	}
	
	private void parseFactory(Factory factory, String line) {
		StringTokenizer st = new StringTokenizer(line, Constants.factoryFileDelimeter);
		st.nextToken(); // description, which should be "Name"
		String name = st.nextToken();
		factory.setName(name);
		int numberOfWorkers = 0;
		try {
			numberOfWorkers = Integer.parseInt(st.nextToken());
			if (numberOfWorkers < 0) {
				numberOfWorkers = 0;
			}
		} catch (Exception ex) {
			// if the numberOfWorkers can't be made into an integer, we will have 0 workers
			Util.printExceptionToCommand(ex);
		}
		factory.setNumberOfWorkers(numberOfWorkers);
		int width = 0;
		int height = 0;
		try {
			width = Integer.parseInt(st.nextToken());
			if (width < 0) {
				width = 0;
			}
			height = Integer.parseInt(st.nextToken());
			if (height < 0) {
				height = 0;
			}
		} catch (Exception ex) {
			Util.printExceptionToCommand(ex);
		}
		factory.setDimensions(width,height);
	}
	
	private void parseTaskBoardLocation(Factory factory, String line) {
		StringTokenizer st = new StringTokenizer(line, Constants.factoryFileDelimeter);
		st.nextToken(); // skip "Task Board"
		int x = 0;
		int y = 0;
		try {
			x = Integer.parseInt(st.nextToken());
			if (x < 0) {
				x = 0;
			}
			y = Integer.parseInt(st.nextToken());
			if (y < 0) {
				y = 0;
			}
		} catch (Exception ex) {
			// if the numberOfWorkers can't be made into an integer, we will have 0 workers
			Util.printExceptionToCommand(ex);
		}
		factory.setTaskBoardLocation(new Point(x,y));
	}
}
