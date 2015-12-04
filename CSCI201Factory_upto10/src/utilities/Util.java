package utilities;

import java.io.PrintStream;
import java.util.Calendar;

public class Util {

	private static void printMessage(PrintStream out, String msg) {
		out.print(Constants.outputFormat.format(Calendar.getInstance().getTime()) + " - ");
		// ste[3] is the method that called this one since it had to come through another
		// method in this class before getting to this one
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		out.print(ste[3].getClassName() + "." + ste[3].getMethodName() + "(...): ");
		out.println(msg);
	}
		
	public static void printExceptionToCommand(Exception ex) {
		printMessage(System.out, ex.getClass().getCanonicalName() + ": " + ex.getMessage());
	}
	
	public static void printMessageToCommand(String msg) {
		printMessage(System.out, msg);
	}
}
