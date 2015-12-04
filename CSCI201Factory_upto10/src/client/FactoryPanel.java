package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class FactoryPanel extends JPanel {
	
	public static final long serialVersionUID = 1;
	
	//The size of the entire panel - auto updates when scaled
	private int mWidth = 0;
	private int mHeight = 0;
	
	//The size of the factory within the panel - panel size minus borders
	private int mFactoryWidth = 0;
	private int mFactoryHeight = 0;
	
	private Graphics mGraphics;
	private Image mImage = null;
	
	public void render(FactorySimulation inFactorySimulation) {
		//render the factory - don't paint directly to avoid screen tearing
		//only want to update things if necessary - otherwise we will do an unnecessary amount of work
		if( mWidth != getWidth() || mHeight != getHeight() || mImage == null) {
			mWidth = getWidth();
			mHeight = getHeight();
			if(mWidth <= 0 || mHeight <= 0) return;
			mImage = createImage(mWidth,mHeight);
			
			//take the border surrounding the factory into account
			mFactoryWidth = mWidth - Constants.factoryBorderSize * 2;
			mFactoryHeight = mHeight - Constants.factoryBorderSize * 2;
			
			//update the scaling of all the objects in the factory
			double width = mFactoryWidth/(double)inFactorySimulation.getWidth();
			double height = mFactoryHeight/(double)inFactorySimulation.getHeight();
			for(FactoryObject object : inFactorySimulation.getObjects()) {
				//re-scale all of the objects, and adjust for borders
				object.resize(width,height);
				object.center(Constants.factoryBorderSize);
			}
		}
		
		if(mImage != null) mGraphics = mImage.getGraphics();
		else return;
		
		if(mGraphics == null) return;
		
		//Clear the entire panel
		mGraphics.setColor(Color.GRAY);
		mGraphics.fillRect(0, 0, mWidth, mHeight);
		
		//Draw the background of the factory floor
		mGraphics.setColor(Color.LIGHT_GRAY);
		mGraphics.fillRect(Constants.factoryBorderSize, Constants.factoryBorderSize, mFactoryWidth, mFactoryHeight);
		
		//Draw the name of the factory
		mGraphics.setColor(Color.BLACK);
		mGraphics.setFont(makeTitleFont());
		int center = (getWidth()/2) - (mGraphics.getFontMetrics().stringWidth(inFactorySimulation.getName())/2);
		mGraphics.drawString(inFactorySimulation.getName(), center, Constants.factoryBorderSize - Constants.factoryBorderSize/4);
		
		//Draw the rest of the factory
		//set the font to the generic font
		mGraphics.setFont(makeTextFont());
		//save the position of the mouse, important to know for drawing tool-tips
		Point mouseLocation = getRelativeMouseLocation();
		
		//Draw nodes - nodes have other objects attached to them
		//when drawing nodes, their object will also be drawn
		for(FactoryNode[] nodes : inFactorySimulation.getNodes()) {
			for(FactoryNode node : nodes) {
				node.draw(mGraphics, mouseLocation);
			}
		}
		
		//Draw workers
		for(FactoryWorker worker : inFactorySimulation.getWorkers()) {
			worker.draw(mGraphics, mouseLocation);
		}
	}
	
	//Actually paint the image rendered onto the panel
	public void paint() {
		Graphics g;
		try{
			g = this.getGraphics();
			if((g != null) && (mImage != null)) {
				g.drawImage(mImage, 0, 0, null);
			}
			//Apparently this is needed for some systems
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		} catch (Exception e) {System.out.println("Graphics context error:" + e);}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		//This isn't part of the main loop, but will be called when re-scaling
		super.paintComponent(g);
		if(mImage != null) {
			g.drawImage(mImage, 0, 0, null);
		}
	}
	
	//Used to force a redraw of the factory - Usually when a new factory is loaded
	//Just set the size to 0 so everything is reset on the next paint call
	public void refresh() {
		mWidth = 0;
		mHeight = 0;
	}
	
	private Font makeTitleFont() {
		//The title needs to fit on the border
		return new Font("TimesRoman", Font.PLAIN, Constants.factoryBorderSize);
	}
	
	private Font makeTextFont() {
		//40.0 is a magic number, it just scaled well with TimesRoman
		//If you use another font, you will need to play around with the number
		return new Font("TimesRoman", Font.PLAIN, (int)(mWidth/40.0));
	}

	private Point getRelativeMouseLocation() {
		//gets the relative mouse location of the JPanel
		Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		mouseLocation.x -= this.getLocationOnScreen().x;
		mouseLocation.y -= this.getLocationOnScreen().y;
		return mouseLocation;
	}
	
}
