package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class FactoryObject {
	
	//This label will be displayed when mouse hovers over object - must be called in draw
	protected String mLabel = "";
	protected Image mImage = null;
	
	protected FactoryObject(Rectangle inDimensions) {
		x = inDimensions.getX();
		y = inDimensions.getY();
		width = inDimensions.width;
		height = inDimensions.height;
	}
	
	//The "true" location and size of the object use this for updating
	private double x = 0, y = 0;
	private int width = 0,height = 0;
	
	private double mXScale;
	private double mYScale;

	//The location and size used for rendering
	protected Rectangle renderBounds = new Rectangle(0,0,0,0);
	private int mFactoryBorderSize = 0;
	
	//Implement in child classes
	public void draw(Graphics g, Point mouseLocation) {
		g.setColor(Color.BLACK);
		g.drawImage(mImage, renderBounds.x, renderBounds.y, renderBounds.width, renderBounds.height, null);
		drawMouseover(g,mouseLocation);
	}
	
	//Draw the mouseOverLabel above the FactoryObject when mouse is over it
	private void drawMouseover(Graphics g, Point mouseLocation) {
		if(renderBounds.contains(mouseLocation)) {
			g.setColor(Color.BLACK);
			g.drawString(mLabel,centerTextX(g,mLabel),renderBounds.y);
		}
	}
	
	//Scale the object for rendering.
	public void resize(double XScale, double YScale) {
		mXScale = XScale; mYScale = YScale;
		renderBounds.x = (int)(x*XScale); renderBounds.y = (int)(y*YScale);
		renderBounds.width = (int)(width*XScale); renderBounds.height = (int)(height*YScale);
	}
	//center the object if needed
	public void center(int inFactoryBordersize) {
		mFactoryBorderSize = inFactoryBordersize;
		renderBounds.y += mFactoryBorderSize;
		renderBounds.x += mFactoryBorderSize;
	}
	
	//Don't need to implement this - some objects are static and don't need to update
	public void update(double deltaTime){}
	
	//These should be used by the child object, hides the extra work of scaling
	protected void changeX(double deltaX) {
		x += deltaX;
		renderBounds.x = (int)(x*mXScale) + mFactoryBorderSize;
	}
	protected void changeY(double deltaY) {
		y += deltaY;
		renderBounds.y = (int)(y*mYScale) + mFactoryBorderSize;
	}
	
	protected boolean moveTowards(FactoryObject factoryObject, double deltaTime) {
		//Navigate to where we want to go
		boolean xMatch = false;
		double moveX = factoryObject.x - this.x;
		if(Math.abs(moveX) <= Constants.closeEnough) {
			this.x = factoryObject.x;
			xMatch = true;
		}
		else if(moveX < 0) this.changeX(deltaTime * -1);
		else if(moveX > 0) this.changeX(deltaTime * +1);
		
		boolean yMatch = false;
		double moveY = factoryObject.y - this.y;
		if(Math.abs(moveY) <= Constants.closeEnough) {
			this.y = factoryObject.y;
			yMatch = true;
		}
		else if(moveY < 0) this.changeY(deltaTime * -1);
		else if(moveY > 0) this.changeY(deltaTime * +1);
		
		return (xMatch & yMatch);
	}
	
	public int getX() {
		return (int)x;
	}
	
	public int getY() {
		return (int)y;
	}
	
	//Helper functions for drawing
	protected int centerTextY(Graphics g) {
		return (int) (renderBounds.y+renderBounds.getHeight()) - (g.getFontMetrics().getHeight()/4);
	}
	protected int centerTextX(Graphics g, String toSize) {
		return (int) (renderBounds.getCenterX() - (g.getFontMetrics().stringWidth(toSize)/2));
	}
}
