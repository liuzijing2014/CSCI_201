package utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {

	public static final DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final int lowPort = 0;
	public static final int highPort = 65535;
	public static final int defaultPort = 6789;
	public static final String defaultHostname = "localhost";
	
	public static final String resourceString = "Resource";
	public static final String productString = "Product";
	public static final String factoryNameString = "Factory Name";
	public static final String taskboardString = "Task Board";
	
}
