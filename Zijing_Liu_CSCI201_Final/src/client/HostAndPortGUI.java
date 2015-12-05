package client;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utilities.Util;

public class HostAndPortGUI extends JFrame {
	public static final long serialVersionUID = 1;
	private JTextField portTextField, hostnameTextField;
	private JLabel descriptionLabel, portLabel, hostnameLabel, errorLabel;
	private JButton connectButton;
	private Lock hostAndPortLock;
	private Condition hostAndPortCondition;
	private Socket socket;

	public HostAndPortGUI() {
		super(Constants.hostAndPortGUITitleString);
		initializeVariables();
		createGUI();
		addActionAdapters();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initializeVariables() {
		socket = null;
		descriptionLabel = new JLabel(Constants.hostAndPortDescriptionString);
		portLabel = new JLabel(Constants.portLabelString);
		hostnameLabel = new JLabel(Constants.hostnameLabelString);
		errorLabel = new JLabel();
		portTextField = new JTextField(20);
		portTextField.setText("" + utilities.Constants.defaultPort);
		hostnameTextField = new JTextField(20);
		hostnameTextField.setText(utilities.Constants.defaultHostname);
		connectButton = new JButton(Constants.connectButtonString);
		hostAndPortLock = new ReentrantLock();
		hostAndPortCondition = hostAndPortLock.newCondition();
	}
	
	private void createGUI() {
		setSize(Constants.hostAndPortGUIwidth, Constants.hostAndPortGUIheight);
		setLayout(new GridLayout(5, 1));
		add(descriptionLabel);
		add(errorLabel);
		JPanel hostFieldPanel = new JPanel();
		hostFieldPanel.setLayout(new FlowLayout());
		hostFieldPanel.add(hostnameLabel);
		hostFieldPanel.add(hostnameTextField);
		add(hostFieldPanel);
		JPanel portFieldPanel = new JPanel();
		portFieldPanel.setLayout(new FlowLayout());
		portFieldPanel.add(portLabel);
		portFieldPanel.add(portTextField);
		add(portFieldPanel);
		add(connectButton);
	}
	
	private void addActionAdapters() {
		class ConnectListener implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				String portStr = portTextField.getText();
				int portInt = -1;
				try {
					portInt = Integer.parseInt(portStr);
				} catch (Exception e) {
					errorLabel.setText(Constants.portErrorString);
					return;
				}
				if (portInt > utilities.Constants.lowPort && portInt < utilities.Constants.highPort) {
					// try to connect
					String hostnameStr = hostnameTextField.getText();
					try {
						socket = new Socket(hostnameStr, portInt);
						hostAndPortLock.lock();
						hostAndPortCondition.signal();
						hostAndPortLock.unlock();
						HostAndPortGUI.this.setVisible(false);
					} catch (IOException ioe) {
						errorLabel.setText(Constants.unableToConnectString);
						Util.printExceptionToCommand(ioe);
						return;
					}
				}
				else { // port value out of range
					errorLabel.setText(Constants.portErrorString);
					return;
				}
			}
		}
		connectButton.addActionListener(new ConnectListener());
		hostnameTextField.addActionListener(new ConnectListener());
		portTextField.addActionListener(new ConnectListener());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
	}
	
	public Socket getSocket() {
		while (socket == null) {
			hostAndPortLock.lock();
			try {
				hostAndPortCondition.await();
			} catch (InterruptedException ie) {
				Util.printExceptionToCommand(ie);
			}
			hostAndPortLock.unlock();
		}
		return socket;
	}
}
