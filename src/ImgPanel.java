import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImgPanel extends JPanel implements ActionListener {

	public static String selectedImg;
	public BufferedImage morphTo, img1, img2, img3;
	public static boolean doMorph;
	
	private static ControlPanel cPanel;
	
	public static boolean showControlPanel = true;
	
	public ImgPanel(Dimension initialSize, ControlPanel cp) 
	{
		super();
		cPanel = cp;
		selectedImg = "Img1";
		doMorph = false;

		setFocusable(true);
	}
	
	public void paintComponenet(Graphics g) {
		cPanel.update(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (selectedImg == "Img1")
			morphTo = img1;
		
		if (selectedImg == "Img2")
			morphTo = img2;
		
		if (selectedImg == "Img3")
			morphTo = img3;
		
		if (doMorph) {
			System.out.println("DO DA MORPHINE");
			doMorph = false;
		}
	}
}





















