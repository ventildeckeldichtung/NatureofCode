package nature2;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PaintStuff {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 public static void concat1(String s1)
	    {
	        s1 = s1 + "forgeeks";
	    }
	 
	    // Concatenates to StringBuilder
	    public static void concat2(StringBuilder s2)
	    {
	        s2.append("forgeeks");
	    }
	 
	    // Concatenates to StringBuffer
	    public static void concat3(StringBuffer s3)
	    {
	        s3.append("forgeeks");
	    }
	 
	public static void main(String argv[]) {
		MyPanel mp = new MyPanel();
		JFrame jf = new JFrame("New Frame");
		jf.add(mp);
		jf.pack();
		jf.setVisible(true);
		
	}

}
