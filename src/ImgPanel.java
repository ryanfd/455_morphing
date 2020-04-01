import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImgPanel extends JPanel implements ActionListener {

	public String selectedImg;
	public static BufferedImage morphTo, img1, img2, img3;
	public static boolean doMorph;
	public static boolean img1Bool, img2Bool, img3Bool;
	
	private static ControlPanel cPanel;
	
	public static boolean showControlPanel = true;
	
	public ImgPanel(Dimension initialSize, ControlPanel cp) 
	{
		super();
		cPanel = cp;
		selectedImg = "Image 1";
		doMorph = false;
		img1Bool = true;
		img2Bool = false;
		img3Bool = false;
		
		try {
			img1 = ImageIO.read(new File("mina_twice.jpg"));
			img2 = ImageIO.read(new File("jy_twice.jpg"));
			img3 = ImageIO.read(new File("sana_twice.jpg"));
			morphTo = ImageIO.read(new File("mina_twice.jpg"));
		} catch (Exception e) {
			System.out.println("Can't load image");
		}

		setFocusable(true);
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = (int) (morphTo.getWidth()/2);
		int h = (int) (morphTo.getHeight()/2);
		int x = (this.getWidth() - morphTo.getWidth(null));
		
		this.setSize(w*5+100,h*4+50);
		
		setBackground(new Color(50,50,50));
		
		g.drawImage(morphTo, x,0,w,h,this);
		System.out.println("again");
		
		if (img1Bool) {
			selectedImg = "Image 1";
		}
		
		if (img2Bool) {
			selectedImg = "Image 2";
		}
		
		if (img3Bool) {
			selectedImg = "Image 3";
		}
		
		
		
		
		if (doMorph) {
			System.out.println("DO DA MORPHY MORPHY"); // do morphing
			doMorph = false;
		}
		
		cPanel.update(this);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("please");
		System.out.println("SYSTEM CHECK");
		// TODO Auto-generated method stub
		if (selectedImg == "Image 1") {
			try {
				morphTo = ImageIO.read(new File("mina_twice.jpg"));
			} catch (Exception e1) {
				System.out.println("Can't load image");
			}
		}
		
		if (selectedImg == "Image 2") {
			try {
				morphTo = ImageIO.read(new File("jy_twice.jpg"));
			} catch (Exception e1) {
				System.out.println("Can't load image");
			}
		}
		
		if (selectedImg == "Image 3") {
			try {
				morphTo = ImageIO.read(new File("sana_twice.jpg"));
			} catch (Exception e1) {
				System.out.println("Can't load image");
			}
		}
		
		repaint();
	}
}





















