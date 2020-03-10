/*File Exercise1.java

 IAT455 - Workshop week 7

 **********************************************************/
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

class Morphing extends Frame { 

	public Morphing() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		this.setTitle("Morphing");
		this.setVisible(true);

		//Anonymous inner-class listener to terminate program
		this.addWindowListener(
				new WindowAdapter(){//anonymous class definition
					public void windowClosing(WindowEvent e){
						System.exit(0);//terminate the program
					}//end windowClosing()
				}//end WindowAdapter
				);//end addWindowListener
	}// end constructor
	
	// accessors
	protected int getRed(int pixel) { return (pixel >>> 16) & 0xFF; }
	protected int getGreen(int pixel) { return (pixel >>> 8) & 0xFF; }
	protected int getBlue(int pixel) { return pixel & 0xFF; }
	
	// vector
	// need to find what exactly this is for
	private int vectorMap(int pixel)
	{
		return pixel;
	}
	
	// create map of composite image based on separated images
	private int compositeMap(int pixel, Operations ops)
	{
		int result;
		
		switch(ops) {
		case subtract: // first image
			result = pixel - vectorMap(pixel);
		case add: // second image
			result = pixel + vectorMap(pixel);
		default:
			result = 0;
		}
		
		return result;
	}

	public void paint(Graphics g) 
	{
		
	}
// =======================================================//

	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FeatureDetection fd = new FeatureDetection();
	
//		Morphing img = new Morphing();// instantiate this object
//		img.repaint();// render the image
	
	}// end main
}
// =======================================================//
