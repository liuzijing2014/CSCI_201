package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class FactoryProgressPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = -3875529770801377779L;
	
	private JTable mTable;
	private TableModel mModel;
	private final String title = "Factory Progress";
	private final String finished = "Completed Products";
	private final String progress = "In-progress Products";
	private final String unstarted = "Unstarted Products";
	final int border = 20;
	
	FactoryProgressPanel(JTable inTable) {
		mTable = inTable;
		mModel = mTable.getModel();
		new Thread(this).start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int w = this.getWidth();
		int h = this.getHeight();
		Font titleFont = new Font("Times New Roman", Font.BOLD|Font.ITALIC, w/24);
		Font labelFont = new Font("Times New Roman", Font.BOLD, w/42);
		
		g.setFont(titleFont);
		int strwidth = g.getFontMetrics(titleFont).stringWidth(title);
		g.drawString(title, (w - strwidth) / 2, g.getFontMetrics(titleFont).getHeight());
		
		double total = 0;
		double started = 0;
		double completed = 0;
		for(int i = 0; i < mModel.getRowCount(); ++i) {
			total += (int)mModel.getValueAt(i, Constants.totalNameIndex);
			started += (int)mModel.getValueAt(i, Constants.startedIndex);
			completed += (int)mModel.getValueAt(i, Constants.completedIndex);
		}
		
		int frameX = border;
		int frameY = border+g.getFontMetrics(titleFont).getHeight();
		int frameW = w-(2*border);
		int frameH = h-g.getFontMetrics(labelFont).getHeight()-g.getFontMetrics(titleFont).getHeight()-(3*border);
		g.drawRect(frameX-1, frameY-1, frameW+1, frameH+1);
		
		int startedWidth = (int) ((started/total)*frameW);
		int completedWidth = (int) ((completed/total)*frameW);
		int noStatusWidth = frameW-(startedWidth+completedWidth);
		
		g.setColor(Color.GREEN);
		g.fillRect(frameX, frameY, completedWidth, frameH);
		g.setColor(Color.YELLOW);
		g.fillRect(frameX + completedWidth, frameY, startedWidth, frameH);
		g.setColor(Color.RED);
		g.fillRect(frameX + completedWidth + startedWidth, frameY, noStatusWidth, frameH);
		g.setColor(Color.BLACK);
		g.setFont(labelFont);
		
		DecimalFormat df = new DecimalFormat(".##");
		int strHeight = g.getFontMetrics(labelFont).getHeight();
		if(completed/total > 0.0) {
			String finishedPercent = df.format(100*(completed/total))+"%";
			int strWidth = g.getFontMetrics(labelFont).stringWidth(finishedPercent);
			g.drawString(finishedPercent, frameX+(completedWidth-strWidth)/2, frameY+(frameH+strHeight)/2);
		}
		if(started/total > 0.0) {
			String progressPercent = df.format(100*(started/total))+"%";
			int strWidth = g.getFontMetrics(labelFont).stringWidth(progressPercent);
			g.drawString(progressPercent, frameX+completedWidth+(startedWidth-strWidth)/2, frameY+(frameH+strHeight)/2);
		}
		if(1-(completed+started)/total > 0.0) {
			String unstartedPercent = df.format(100*(1-(completed+started)/total))+"%";
			int strWidth = g.getFontMetrics(labelFont).stringWidth(unstartedPercent);
			g.drawString(unstartedPercent, frameX+completedWidth+startedWidth+(noStatusWidth-strWidth)/2, frameY+(frameH+strHeight)/2);
		}
		
		int legendX = border;
		int legendY = frameY+frameH+border;
		int legendW = w-(2*border);
		int legendH = g.getFontMetrics(labelFont).getHeight();
		int boxAndLabelW = (legendW-(2*border))/3;
		int boxW = boxAndLabelW-g.getFontMetrics(labelFont).stringWidth(progress)-5;		
		
		g.drawRect(legendX-1, legendY-1, boxW+1, legendH+1);
		g.drawString(unstarted, legendX+boxW+5, legendY+legendH);
		g.drawRect(legendX+boxAndLabelW+border-1, legendY-1, boxW+1, legendH+1);
		g.drawString(progress, legendX+boxAndLabelW+border+boxW+5, legendY+legendH);
		g.drawRect(legendX+(2*boxAndLabelW)+(2*border)-1, legendY-1, boxW+1, legendH+1);
		g.drawString(finished, legendX+(2*boxAndLabelW)+(2*border)+boxW+5, legendY + legendH);
		g.setColor(Color.RED);
		g.fillRect(legendX, legendY, boxW, legendH);
		g.setColor(Color.YELLOW);
		g.fillRect(legendX+boxAndLabelW+border, legendY, boxW, legendH);
		g.setColor(Color.GREEN);
		g.fillRect(legendX+(2*boxAndLabelW)+(2*border), legendY, boxW, legendH);
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(33);
				repaint();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}	
	}
}
