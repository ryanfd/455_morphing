import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ImgPanel extends JPanel implements ActionListener {

	private Timer t;
	public boolean addFood;
	public static boolean selectAll = false; //boolean for shift + 'D' display toggle
	public static boolean showControlPanel = true; //hide/show control panel

	public String selectedImg = "Image 1";
	public String prevImg;
	public static BufferedImage morphTo, img1, img2, img3;
	public static boolean doMorph;
	public static boolean img1Bool, img2Bool, img3Bool;

	private static ControlPanel cPanel;

	private static String status = "Status";
	
	int w,h,x;

	public ImgPanel(Dimension initialSize, ControlPanel cp) {
		// TODO Auto-generated constructor stub
		super();

		cPanel = cp;
		
		selectedImg = "Image 1";
		prevImg = "Image 1";
		
		try {
			img1 = ImageIO.read(new File("mina_twice.jpg"));
			img2 = ImageIO.read(new File("jy_twice.jpg"));
			img3 = ImageIO.read(new File("sana_twice.jpg"));
			morphTo = ImageIO.read(new File("mina_twice.jpg"));
			w = (int) (morphTo.getWidth()/2);
			h = (int) (morphTo.getHeight()/2);
			x = (this.getWidth() - morphTo.getWidth(null));
			this.setSize(w*5+100,h*4+50);
			System.out.println("images read");
		} catch (Exception e) {
			System.out.println("Can't load image");
		}
		

		int randomSpeedx = (int) (Math.random()*14 - 7);
		if (randomSpeedx == 0) randomSpeedx = 7;

		int randomSpeedy = (int) (Math.random()*14 - 7);
		if (randomSpeedy == 0) randomSpeedx = 7;

		
		// DO NOT TOUCH THE TIMER
		// don't know why this timer is necessary, but it is
		t = new Timer(33, this);
		t.start();

		setFocusable(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		w = (int) (morphTo.getWidth()/2);
		h = (int) (morphTo.getHeight()/2);
		x = (this.getWidth() - morphTo.getWidth(null));
		this.setSize(w*5+100,h*4+50);
		g.drawImage(morphTo, x,0,w,h,this);
		cPanel.update(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String imgAddress = "mina_twice.jpg";
		
		if (img1Bool) {
			selectedImg = "Image 1";
			imgAddress = "mina_twice.jpg";
			System.out.println(selectedImg);
			img1Bool = false;
		}
		
		else if (img2Bool) {
			selectedImg = "Image 2";
			imgAddress = "jy_twice.jpg";
			System.out.println(selectedImg);
			img2Bool = false;
		}
		
		else if (img3Bool) {
			selectedImg = "Image 3";
			imgAddress = "sana_twice.jpg";
			System.out.println(selectedImg);
			img3Bool = false;
		}
		
		if (doMorph) { //boolean for initiating panic mode of highlighted animal
			System.out.println("Morphing Time");
			if (selectedImg != prevImg) {
				System.out.println("test");
				try {
					morphTo = ImageIO.read(new File(imgAddress));
				} catch (Exception e1) {
					System.out.println("Can't load image");
				}
			}
			doMorph = false;
			prevImg = selectedImg;
		}
		
		repaint();
	}

	public static void setStatus(String st) {
		status = st;
	}

	public String getStatus() {
		return status;
	}
}

	



//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.image.BufferedImage;
//import java.io.File;
//
//import javax.imageio.ImageIO;
//import javax.swing.JPanel;
//
//import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;
//
//public class ImgPanel extends JPanel implements ActionListener {
//
//	public String selectedImg = "Image 1";
//	public static BufferedImage morphTo, img1, img2, img3;
//	public static boolean doMorph;
//	public static boolean img1Bool, img2Bool, img3Bool;
//	
//	private static ControlPanel cPanel;
//	
//	public static boolean showControlPanel = true;
//	
//	public ImgPanel(Dimension initialSize, ControlPanel cp)  
//	{
//		super();
//		cPanel = cp;
////		selectedImg = "Image 1";
//		doMorph = false;
//		img1Bool = true;
//		img2Bool = false;
//		img3Bool = false;
//		
//		try {
//			img1 = ImageIO.read(new File("mina_twice.jpg"));
//			img2 = ImageIO.read(new File("jy_twice.jpg"));
//			img3 = ImageIO.read(new File("sana_twice.jpg"));
//			morphTo = ImageIO.read(new File("mina_twice.jpg"));
//		} catch (Exception e) {
//			System.out.println("Can't load image");
//		}
//
//		setFocusable(true);
//	}
//	
//	
//	public void paintComponent(Graphics g) {
//		int w = (int) (morphTo.getWidth()/2);
//		int h = (int) (morphTo.getHeight()/2);
//		int x = (this.getWidth() - morphTo.getWidth(null));
//		
//		this.setSize(w*5+100,h*4+50);
//		
//		setBackground(new Color(50,50,50));
//		
//		if (selectedImg == "Image 1") {
//			try {
//				morphTo = ImageIO.read(new File("mina_twice.jpg"));
//			} catch (Exception e1) {
//				System.out.println("Can't load image");
//			}
//		}
//		
//		if (selectedImg == "Image 2") {
//			try {
//				morphTo = ImageIO.read(new File("jy_twice.jpg"));
//			} catch (Exception e1) {
//				System.out.println("Can't load image");
//			}
//		}
//		
//		if (selectedImg == "Image 3") {
//			try {
//				morphTo = ImageIO.read(new File("sana_twice.jpg"));
//			} catch (Exception e1) {
//				System.out.println("Can't load image");
//			}
//		}
//		
//		g.drawImage(morphTo, x,0,w,h,this);
//		System.out.println("again");
//		
//		
//		
//		cPanel.update(this);
//	}
//
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		// TODO Auto-generated method stub
//		String ch = e.getActionCommand();
//		
//		System.out.println("SYSTEM CHECK");
//		if (img1Bool) {
//			selectedImg = "Image 1";
//			img2Bool = false;
//			img3Bool = false;
//		}
//		
//		if (img2Bool) {
//			selectedImg = "Image 2";
//			img1Bool = false;
//			img3Bool = false;
//		}
//		
//		if (img3Bool) {
//			selectedImg = "Image 3";
//			img2Bool = false;
//			img1Bool = false;
//		}
//		
//		if (doMorph) {
//			System.out.println("DO DA MORPHY MORPHY"); // do morphing
//			doMorph = false;
//		}
//		
//		
//		repaint();
//		
//	}
//	
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
