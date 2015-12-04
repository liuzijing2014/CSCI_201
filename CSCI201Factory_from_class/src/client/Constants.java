package client;

import java.awt.Toolkit;

public class Constants {

	public static final String hostAndPortGUITitleString = "Host and Port GUI";
	public static final String hostAndPortDescriptionString = "<html>Enter the hostname and port number of the server</html>";
	public static final String portLabelString = "Port";
	public static final String hostnameLabelString = "Hostname";
	public static final String connectButtonString = "Connect";
	public static final int hostAndPortGUIwidth = 330;
	public static final int hostAndPortGUIheight = 200;
	public static final String portErrorString = "<html><font color=\"red\">Please enter a valid port<br />between " + utilities.Constants.lowPort + " and " + utilities.Constants.highPort + "</font></html>";
	public static final String unableToConnectString = "<html><font color=\"red\">Unable to connect to host.</font></html>";
	public static final String unableToGetStreams = "Could not get reader and writer for socket";
	
	public static final String factoryGUITitleString = "Factory Client";
	public static final int factoryGUIheight = Toolkit.getDefaultToolkit().getScreenSize().height - 100;
	public static final int factoryGUIwidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int factoryBorderSize = 30;
	public static final int tableWidth = 200;
	
	public static final String[] tableColumnNames = {"Product", "Total", "Started", "Completed"};
	public static final int productNameIndex = 0;
	public static final int totalNameIndex = 1;
	public static final int startedIndex = 2;
	public static final int completedIndex = 3;
	public static final String startedString = "X";
	public static final String completedString = "X";
	
	public static final String waitingForFactoryConfigMessage = "Waiting for factory configuration from server...";
	public static final String serverCommunicationFailed = "Communication with the server failed.";
	public static final String factoryReceived = "Factory received.";
	public static final String resourceReceived = "Resource received.";
	public static final String workerString = "Worker ";
	public static final String startedBuildingString = " started building ";
	public static final String finishedBuildingString = " finished building ";
	public static final String assignedString = " has been assigned product ";
	
	public static final String resourceFolder = "resources/img/";
	public static final String png = ".png";
	
	public static final int workerSpeed = 3; //nodes per second
	public static final int resourcesYPosition = 100;
	public static final int workerWidth = 25;
	public static final int workerHeight = 25;
	
	public static final int simulation_0x = 0;
	public static final int simulation_1x = 1;
	public static final int simulation_2x = 2;
	public static final int simulation_3x = 3;
	
	public static final double closeEnough = 0.05; //used to see when a worker has gotten close enough to a node
	
}
