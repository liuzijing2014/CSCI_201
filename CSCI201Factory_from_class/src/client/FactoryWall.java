package client;

import java.awt.Rectangle;

import libraries.ImageLibrary;

public class FactoryWall extends FactoryObject {
	
	public FactoryWall(Rectangle inDimensions, String file)
	{
		super(inDimensions);
		mImage = ImageLibrary.getImage(file);
	}
}
