package client;

import java.net.Socket;

public class FactoryClient {
	
	public FactoryClient() {
		HostAndPortGUI hapgui = new HostAndPortGUI();
		Socket socket = hapgui.getSocket();
		new FactoryClientGUI(socket);
	}
	
	public static void main(String [] args) {
		new FactoryClient();
	}
}
