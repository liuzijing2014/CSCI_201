package server;

public class Constants {

	public static final String portDescriptionString = "<html>Enter the port number on which<br />you would like the server to listen.</html>";
	public static final String portLabelString = "Port";
	public static final String submitPortString = "Start Listening";
	public static final String portGUITitleString = "Factory Server - Port";
	public static final int portGUIwidth = 300;
	public static final int portGUIheight = 170;
	public static final String portErrorString = "<html><font color=\"red\">Please enter a valid port<br />between " + utilities.Constants.lowPort + " and " + utilities.Constants.highPort + "</font></html>";
	public static final String portAlreadyInUseString = "<html><font color=\"red\">Port already in use.  Select another port<br />between " + utilities.Constants.lowPort + " and " + utilities.Constants.highPort + "</font></html>";
	
	public static final String initialFactoryTextAreaString = "Waiting for connections on port ";
	public static final String factoryGUITitleString = "Factory Server";
	public static final int factoryGUIwidth = 500;
	public static final int factoryGUIheight = 500;
	
	public static final String startClientConnectedString = "Client with IP address ";
	public static final String endClientConnectedString = " connected.";
	public static final String clientDisconnected = "Factory Client disconnected.";
	
	public static final String selectFactoryButtonString = "Select Factory";
	public static final String defaultResourcesDirectory = "resources/";

	public static final String unrecognizedLine = "Unrecognized line in file: ";
	public static final String factoryFileDelimeter = "|";
	public static final String factoryLoadedMessage = "Factory finished loading!";
}
