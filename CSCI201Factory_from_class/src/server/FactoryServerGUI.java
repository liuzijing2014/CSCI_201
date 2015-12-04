package server;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class FactoryServerGUI extends JFrame {
	public static final long serialVersionUID = 1;
	private static JTextArea textArea;
	private JScrollPane textAreaScrollPane;
	private JButton selectFactoryButton;
	private JComboBox<String> selectFactoryComboBox;
	private JLabel clientCountLabel;
	private JSpinner numClientSpinner;

	public FactoryServerGUI() {
		super(Constants.factoryGUITitleString);
		initializeVariables();
		createGUI();
		addActionAdapters();
		setVisible(true);
	}
	
	private void initializeVariables() {
		textArea = new JTextArea();
		textArea.setEditable(false);
		textAreaScrollPane = new JScrollPane(textArea);

		Vector<String> filenamesVector = new Vector<String>();
		File directory = new File(Constants.defaultResourcesDirectory);
		File[] filesInDirectory = directory.listFiles();
		for (File file : filesInDirectory) {
		    if (file.isFile()) {
		    	filenamesVector.add(file.getName());
		    }
		}
		selectFactoryButton = new JButton(Constants.selectFactoryButtonString);
		selectFactoryComboBox = new JComboBox<String>(filenamesVector);
		clientCountLabel = new JLabel(Constants.clientCountString);
		SpinnerModel model = new SpinnerNumberModel(1, 1, Constants.maxNumClients, 1);
		numClientSpinner = new JSpinner(model);
	}
	
	private void createGUI() {
		setSize(Constants.factoryGUIwidth, Constants.factoryGUIheight);
		numClientSpinner.setPreferredSize(selectFactoryComboBox.getPreferredSize());
		
		JPanel northPanel = new JPanel();
		northPanel.add(clientCountLabel);
		northPanel.add(numClientSpinner);
		northPanel.add(selectFactoryComboBox);
		northPanel.add(selectFactoryButton);
		add(northPanel, BorderLayout.NORTH);
		add(textAreaScrollPane, BorderLayout.CENTER);
	}
	
	private void addActionAdapters() {
		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  System.exit(0);
			  }
		});
		
		selectFactoryButton.addActionListener(new ConfigureFactoryListener(selectFactoryComboBox, numClientSpinner));
	}
	
	public static void addMessage(String msg) {
		if (textArea.getText() != null && textArea.getText().trim().length() > 0) {
			textArea.append("\n" + msg);
		}
		else {
			textArea.setText(msg);
		}
	}
}
