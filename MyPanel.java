package nature2;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MyPanel extends JPanel{
	MyPanel(){
		this.setPreferredSize(new Dimension(800,600));
	}
	@Override
	public void paint(Graphics g) {
		g.drawLine(0, 0, 800, 600);
	}
}
