package client;

import java.io.FileWriter;
import java.io.IOException;

public interface FactoryReporter {
	public void report(FileWriter fw) throws IOException;
}
