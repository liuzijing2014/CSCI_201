package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GUIShapeThreads extends JFrame {
	public static final long serialVersionUID = 1;
	private JButton jbSquare, jbCircle;
	private SquarePanel sp;
	private RoundPanel rp;
	public GUIShapeThreads() {
		super("GUI Shape Threads");
		setSize(300, 300);
		setLocation(850, 50);
		setLayout(null);
		jbSquare = new JButton("Change Rectangle Color");
		jbSquare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				sp.changeColor();
			}
		});
		Dimension jbSquareDimensions = jbSquare.getPreferredSize();
		jbSquare.setBounds(60, 0, jbSquareDimensions.width, jbSquareDimensions.height);
		add(jbSquare);

		jbCircle = new JButton("Change Circle Color");
		jbCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				rp.changeColor();
			}
		});
		Dimension jbCircleDimensions = jbCircle.getPreferredSize();
		jbCircle.setBounds(75, 30, jbCircleDimensions.width, jbCircleDimensions.height);
		add(jbCircle);

		sp = new SquarePanel();
		sp.setBounds(0, 50, 50, 50);
		add(sp);
		Thread t = new Thread(sp);
		t.start();

		rp = new RoundPanel();
		rp.setBounds(200, 50, 50, 50);
		add(rp);
		Thread t1 = new Thread(rp);
		t1.start();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	class SquarePanel extends JPanel implements Runnable {
		public static final long serialVersionUID = 1;
		private Color colorArray[] = new Color[]{Color.BLACK, Color.BLUE, Color.GREEN, Color.YELLOW};
		private int colorIndex = 0;
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(colorArray[colorIndex % 4]);
			g.fillRect(0, 0, 50, 50);
		}
		public void changeColor() {
			colorIndex++;
			repaint();
		}
		public void run() {
			try {
				for (int i=30; i < 200; i+=4) {
					for (int j=60; j < 200; j+=2) {
						sp.setBounds(i, j, 50, 50);
						Thread.sleep(100);
					}
				}
			} catch (InterruptedException ie) {
				System.out.println("ie: " + ie.getMessage());
			}
		}
	}

	class RoundPanel extends JPanel implements Runnable {
		public static final long serialVersionUID = 1;
		private Color colorArray[] = new Color[]{Color.BLACK, Color.BLUE, Color.GREEN, Color.YELLOW};
		private int colorIndex = 0;
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(colorArray[colorIndex % 4]);
			g.fillOval(0, 0, 50, 50);
		}
		public void changeColor() {
			colorIndex++;
			repaint();
		}
		public void run() {
			try {
				for (int i=200; i > 0; i-=4) {
					for (int j=200; j > 60; j-=4) {
						rp.setBounds(i, j, 50, 50);
						Thread.sleep(100);
					}
				}
			} catch (InterruptedException ie) {
				System.out.println("ie: " + ie.getMessage());
			}
		}
	}


	public static void main(String [] args) {
		GUIShapeThreads guit = new GUIShapeThreads();
		guit.setVisible(true);
	}
}
