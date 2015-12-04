package client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class FactoryClientGUI extends JFrame {
	
	class FactoryController extends JFrame implements ChangeListener, WindowStateListener {
		private static final long serialVersionUID = 7573324399771995953L;
		
		private JTabbedPane tabbedPane;
		private JButton pauseButton;
		private JButton continueButton;
		private JButton resetButton;
		
		private int speed = Constants.simulation_1x;
		private int windowSaveSpeed = Constants.simulation_1x;
		
		public FactoryController() {
			super("Factory Controller");
			setSize(320, 240);
			tabbedPane = new JTabbedPane();
			tabbedPane.addKeyListener(new FactorySpeedImplementer());
			
			createTimePanel();
			createOtherPanel();
			add(tabbedPane);
			
			setVisible(false);
			setLocationRelativeTo(null);
		}
		
		private void createTimePanel() {
			JPanel timePanel = new JPanel();
			timePanel.setLayout(new BorderLayout());
			
			Dictionary<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(Constants.simulation_0x, new JLabel("Paused"));
			labelTable.put(Constants.simulation_1x, new JLabel("Normal"));
			labelTable.put(Constants.simulation_2x, new JLabel("Double"));
			labelTable.put(Constants.simulation_3x, new JLabel("Triple"));
			simulationSpeedController.setLabelTable(labelTable);
			simulationSpeedController.setPaintLabels(true);		
			simulationSpeedController.setBorder(new TitledBorder("Speed Controller"));
			timePanel.add(simulationSpeedController, BorderLayout.SOUTH);
			
			simulationSpeedController.addChangeListener(this);
			
			JPanel buttonBox = new JPanel();
			buttonBox.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = 1;
			pauseButton = new JButton("Pause");
			pauseButton.addKeyListener(new FactorySpeedImplementer());
			pauseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					KeyListener [] keyListeners = pauseButton.getKeyListeners();
					((FactorySpeedImplementer) keyListeners[0]).pauseFactory();
				}
			});
			gbc.gridy = 1;
			buttonBox.add(pauseButton, gbc);
			continueButton = new JButton("Continue");
			continueButton.setEnabled(false);
			continueButton.addKeyListener(new FactorySpeedImplementer());
			continueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					KeyListener [] keyListeners = pauseButton.getKeyListeners();
					((FactorySpeedImplementer) keyListeners[0]).continueFactory();
				}
			});
			gbc.gridy = 2;
			buttonBox.add(continueButton, gbc);
			resetButton = new JButton("Reset");
			resetButton.addKeyListener(new FactorySpeedImplementer());
			resetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					KeyListener [] keyListeners = pauseButton.getKeyListeners();
					((FactorySpeedImplementer) keyListeners[0]).resetFactory();
				}
			});
			gbc.gridy = 3;
			buttonBox.add(resetButton, gbc);
			timePanel.add(buttonBox);
			
			tabbedPane.add("Time", timePanel);
		}
		
		private void createOtherPanel() {
			JPanel otherPanel = new JPanel();
			tabbedPane.add("Other", otherPanel);
		}

		@Override
		public void stateChanged(ChangeEvent ce) {
			int state = ((JSlider)ce.getSource()).getValue();
			if(state == Constants.simulation_0x) {
				continueButton.setEnabled(true);
				pauseButton.setEnabled(false);
			} else {
				continueButton.setEnabled(false);
				pauseButton.setEnabled(true);
			}
		}

		@Override
		public void windowStateChanged(WindowEvent we) {
			int state = we.getNewState();
			if((state & Frame.ICONIFIED) == Frame.ICONIFIED) {
				setVisible(false);
				windowSaveSpeed = simulationSpeedController.getValue();
				simulationSpeedController.setValue(Constants.simulation_0x);
			} else {
				simulationSpeedController.setValue(windowSaveSpeed);
			}			
		}
	}
	
	class FactorySpeedImplementer extends KeyAdapter {
		public void keyPressed(KeyEvent ke) {
			int keyCode = ke.getKeyCode();
			if(keyCode == KeyEvent.VK_P) {
				pauseFactory();
			} else if(keyCode == KeyEvent.VK_C) {
				continueFactory();
			} else if(keyCode == KeyEvent.VK_R) {
				resetFactory();
			} else if(keyCode == KeyEvent.VK_LEFT) {
				int currSpeed = simulationSpeedController.getValue();
				if(currSpeed != Constants.simulation_0x) {
					--currSpeed;
					simulationSpeedController.setValue(currSpeed);
				}
			} else if(keyCode == KeyEvent.VK_RIGHT) {
				int currSpeed = simulationSpeedController.getValue();
				if(currSpeed != Constants.simulation_3x) {
					++currSpeed;
					simulationSpeedController.setValue(currSpeed);
				}
			}
		}
		
		public void pauseFactory() {
			if (simulationSpeedController.getValue() != Constants.simulation_0x) {
				factoryController.continueButton.setEnabled(true);
				factoryController.pauseButton.setEnabled(false);
				factoryController.speed = simulationSpeedController.getValue();
				simulationSpeedController.setValue(Constants.simulation_0x);
			}
		}
		
		public void continueFactory() {
			factoryController.continueButton.setEnabled(false);
			factoryController.pauseButton.setEnabled(true);
			simulationSpeedController.setValue(factoryController.speed);
		}
		
		public void resetFactory() {
			factoryController.continueButton.setEnabled(false);
			factoryController.pauseButton.setEnabled(true);
			factoryController.speed = Constants.simulation_1x;
			simulationSpeedController.setValue(Constants.simulation_1x);
			factoryManager.reset();
		}
	}
	
	public static final long serialVersionUID = 1;
	
	private FactoryPanel factoryPanel;
	private FactoryManager factoryManager;

	private JTextArea messageTextArea;
	private JTable productTable;
	private DefaultTableModel productTableModel;
	private JScrollPane tableScrollPane;
	private JSlider simulationSpeedController;
	
	private FactoryController factoryController;
	
	FactoryClientGUI(Socket socket){
		super(Constants.factoryGUITitleString);
		factoryManager = new FactoryManager();
		initializeVariables();
		createMenu();
		createGUI();
		new FactoryClientListener(factoryManager, this, socket);
		addActionAdapters();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void initializeVariables() {
		messageTextArea = new JTextArea(10, 50);
		factoryPanel = new FactoryPanel();
		factoryManager.setFactoryRenderer(factoryPanel);
		
		productTableModel = new DefaultTableModel(null, Constants.tableColumnNames);
		productTable = new JTable(productTableModel);
		productTable.setEnabled(false);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(productTableModel);
		productTable.setRowSorter(sorter);
		tableScrollPane = new JScrollPane(productTable);
		tableScrollPane.setBounds(Constants.factoryGUIwidth - Constants.tableWidth - 10, 0, Constants.tableWidth, Constants.factoryGUIheight - 100);
		
		simulationSpeedController = new JSlider(JSlider.HORIZONTAL,Constants.simulation_0x, Constants.simulation_3x, Constants.simulation_1x);
		simulationSpeedController.addChangeListener(factoryManager);
		simulationSpeedController.setMajorTickSpacing(1);
		simulationSpeedController.setMinorTickSpacing(1);
		simulationSpeedController.setPaintTicks(true);
		simulationSpeedController.addKeyListener(new FactorySpeedImplementer());
		
		factoryController = new FactoryController();
		addWindowStateListener(factoryController);
	}
	
	private void createGUI() {
		setSize(Constants.factoryGUIwidth, Constants.factoryGUIheight);
		setLayout(new BorderLayout());
		JScrollPane messageTextAreaScrollPane = new JScrollPane(messageTextArea);
		
		Box bottomBox = Box.createHorizontalBox();
		bottomBox.add(messageTextAreaScrollPane);
		bottomBox.add(new FactoryProgressPanel(productTable));
		
		add(factoryPanel,BorderLayout.CENTER);
		add(bottomBox, BorderLayout.SOUTH);
		add(tableScrollPane,BorderLayout.EAST);
	}
	
	public JTable getTable() {
		return productTable;
	}
	
	private void addActionAdapters() {
		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  System.exit(0);
			  }
		});
		messageTextArea.addKeyListener(new FactorySpeedImplementer());
	}
	
	public void addMessage(String msg) {
		if (messageTextArea.getText().length() != 0) {
			messageTextArea.append("\n");
		}
		messageTextArea.append(msg);
	}
	
	private void createMenu() {
		JMenuBar menu = new JMenuBar();
		JMenuItem controller = new JMenuItem("Controller");
		controller.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				factoryController.setVisible(true);
			}
			
		});
		menu.add(controller);
		setJMenuBar(menu);
	}
}
