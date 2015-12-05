package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import client.Constants;
import libraries.ImageLibrary;
import resource.Factory;
import resource.Product;
import resource.Resource;

public class FactoryInfoFrame extends JDialog{
	private static final long serialVersionUID = -5526479011741091932L;
	
	Factory factory;
	
	FactoryInfoPanel factoryInfoPanel;
	Vector<ResourceInfoPanel> resourceInfoPanels;
	Vector<ProductInfoPanel> productInfoPanels;
	
	FactoryInfoFrame(Factory factory) {
		this.factory = factory;
		setTitle(factory.getName() + " information");
		setSize(640,480);
		initializeComponents();
		createGUI();
		createMenu();
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initializeComponents() {
		factoryInfoPanel = new FactoryInfoPanel(factory);
		
		resourceInfoPanels = new Vector<ResourceInfoPanel>(factory.getResources().size());
		for(Resource r : factory.getResources()) {
			resourceInfoPanels.add(new ResourceInfoPanel(r));
		}
		
		productInfoPanels = new Vector<ProductInfoPanel>(factory.getProducts().size());
		for(Product p : factory.getProducts()) {
			productInfoPanels.add(new ProductInfoPanel(p));
		}
	}
	
	private void createGUI() {
		add(factoryInfoPanel,BorderLayout.NORTH);
		
		JPanel splitPanel = new JPanel(new GridLayout(0,2));
		
		JPanel rPanel = new JPanel(new BorderLayout());
		JPanel resourcePanel = new JPanel();
		resourcePanel.setLayout(new BoxLayout(resourcePanel,BoxLayout.Y_AXIS));
		
		for(ResourceInfoPanel r : resourceInfoPanels) {
			resourcePanel.add(r);
		}
		
		JScrollPane rsp = new JScrollPane(resourcePanel);
		rsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		rsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel pPanel = new JPanel(new BorderLayout());
		JPanel productPanel = new JPanel();
		productPanel.setLayout(new BoxLayout(productPanel,BoxLayout.Y_AXIS));
		
		for(ProductInfoPanel p : productInfoPanels) {
			productPanel.add(p);
		}
		
		JScrollPane psp = new JScrollPane(productPanel);
		psp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		psp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		rPanel.add(new JLabel("Available Resources"),BorderLayout.NORTH);
		pPanel.add(new JLabel("Available Products"),BorderLayout.NORTH);
		
		rPanel.add(rsp);
		pPanel.add(psp);
		
		splitPanel.add(rPanel);
		splitPanel.add(pPanel);
		add(splitPanel);
	}
	
	private void createMenu() {
		JMenuBar menu = new JMenuBar();
		JMenuItem about = new JMenuItem("About");
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
		about.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null,
						"Professor Miller",
						"Summer 2015 - Midterm",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(about);
		setJMenuBar(menu);
	}
	
	class FactoryInfoPanel extends JPanel{
		private static final long serialVersionUID = -8834403216008662530L;
		{
			setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
			setBorder(new LineBorder(Color.BLACK));
		}
		FactoryInfoPanel(Factory factory) {
			add(Box.createGlue());
			add(new JLabel("Width: "+factory.getWidth()+ " Height: "+factory.getHeight()));
			add(Box.createGlue());
			add(new JLabel("TaskBoard Location: "+"("+factory.getTaskBoardLocation().x+","+factory.getTaskBoardLocation().y+")"));
			add(Box.createGlue());
		}
	}
	
	class ResourceInfoPanel extends JPanel{
		private static final long serialVersionUID = 3308372526611814757L;
		
		{
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		}
		
		ResourceInfoPanel(Resource resource) {
			Image img = ImageLibrary.getImage(Constants.resourceFolder + resource.getName() + Constants.png);
			setBorder(new TitledBorder(resource.getName()));
			
			JPanel infoPanel = new JPanel() {
				private static final long serialVersionUID = 3633965730494654795L;
				{
					setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				}
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(img.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH), 0, 0, this);
				}
			};
			
			JPanel amountPanel = new JPanel() {
				private static final long serialVersionUID = -7422492056842540887L;
				{
					add(new JLabel("Amount: "+resource.getQuantity()));
				}
				@Override
				protected void paintComponent(Graphics g) {}
			};
			
			JPanel locationPanel = new JPanel() {
				private static final long serialVersionUID = -7422492056842540887L;
				{
					add(new JLabel("Location: "+"("+resource.getX()+","+resource.getY()+")"));
				}
				@Override
				protected void paintComponent(Graphics g) {}
			};
			
			infoPanel.add(amountPanel);
			infoPanel.add(locationPanel);
			add(infoPanel);
		}
	}
	
	class ProductInfoPanel extends JPanel{
		private static final long serialVersionUID = 3308372526611814757L;
		
		{
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		}
		
		ProductInfoPanel(Product product) {
			setBorder(new TitledBorder(product.getName()));
			for(Resource r : product.getResourcesNeeded()) {
				JPanel panel = new JPanel();
				panel.add(new JLabel(r.getQuantity()+" "+r.getName()));
				add(panel);
			}
		}
	}

}
