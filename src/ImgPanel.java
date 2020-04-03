import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ImgPanel extends JPanel implements ActionListener {

	private Timer t;
	public boolean addFood;
	public static boolean selectAll = false; //boolean for shift + 'D' display toggle
	public static boolean showControlPanel = true; //hide/show control panel

	public String selectedImg;
	public String prevImg;
	public static BufferedImage morphTo, img1, img2, img3;
	public static boolean doMorph;
	public static boolean img1Bool, img2Bool, img3Bool;
	public static boolean canUpdate = true;

	private JLabel displayImg;
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
		} catch (Exception e) {
			System.out.println("Can't load image");
		}

		BufferedImage displayBuff = getScaledImage(morphTo, (int) (morphTo.getWidth()/2.2), (int) (this.getHeight()/4.3));
		ImageIcon displayIcon = new ImageIcon(displayBuff);
		displayImg = new JLabel(displayIcon);

		// DO NOT TOUCH THE TIMER
		// timer allows for updated frames: 30 FPS
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
		//		g.drawImage(morphTo, x,0,w,h,this);
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(displayImg);
		cPanel.update(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String imgAddress = "mina_twice.jpg";

		if (img1Bool) {
			selectedImg = "Image 1";
			imgAddress = "mina_twice.jpg";
			img1Bool = false;
		}

		if (img2Bool) {
			selectedImg = "Image 2";
			imgAddress = "jy_twice.jpg";
			img2Bool = false;
		}

		if (img3Bool) {
			selectedImg = "Image 3";
			imgAddress = "sana_twice.jpg";
			img3Bool = false;
		}

		if (doMorph) { //boolean for initiating morph
			if (selectedImg != prevImg) {
				BufferedImage newImg = null;
				if (selectedImg == "Image 1") {
					System.out.println("img1");
					newImg = getScaledImage(img1, (int) (img1.getWidth()/2.2), (int) (this.getHeight()/4.3));
					ImageIcon result = new ImageIcon(newImg);
					displayImg.setIcon(result);
				} 
				if (selectedImg == "Image 2") {
					System.out.println("img2");
					newImg = getScaledImage(img2, (int) (img2.getWidth()/2.2), (int) (this.getHeight()/4.3));
					ImageIcon result = new ImageIcon(newImg);
					displayImg.setIcon(result);
				} 
				if (selectedImg == "Image 3") {
					System.out.println("img3");
					newImg = getScaledImage(img3, (int) (img3.getWidth()/2.2), (int) (this.getHeight()/4.3));
					ImageIcon result = new ImageIcon(newImg);
					displayImg.setIcon(result);
				}
			} else {
				System.out.println("Image already selected!");
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

	private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h)
	{
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}