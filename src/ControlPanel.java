import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

//control panel shows internal data of selected animal, as well as lets user alter program and objects
public class ControlPanel extends JPanel {
	
	private Container bottom, top;
	private JButton img1, img2, img3;
	private JButton morphButton;
	private JLabel label;
	private JTextField field, field2, field3;
	private Container center;
	private String currentImg;

	public ControlPanel() {
		super();
		setBorder(new TitledBorder("Control Panel"));
		setComponentsAtrributes();
		setLayout(new BorderLayout());
		add(bottom, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
	
	
		label = new JLabel("Selected Image");
		field = new JTextField(5);
		field.setFocusable(false);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field2 = new JTextField(5);
		field2.setFocusable(false);
		field2.setHorizontalAlignment(SwingConstants.CENTER);
		field3 = new JTextField(5);
		field3.setFocusable(false);
		field3.setHorizontalAlignment(SwingConstants.CENTER);
		center = new Container();
		center.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
		center.add(label);
		center.add(field);
		center.add(field2);
		center.add(field3);
	
		add(center, BorderLayout.CENTER);
	}

	private void setComponentsAtrributes() {
		BufferedImage buff1 = null; 
		BufferedImage buff2 = null; 
		BufferedImage buff3 = null;
		// create img buttons
		try {
			buff1 = ImageIO.read(new File("mina_twice.jpg"));
			buff2 = ImageIO.read(new File("jy_twice.jpg"));
			buff3 = ImageIO.read(new File("sana_twice.jpg"));
		} catch (Exception e) {
			System.out.println("Can't load image");
		}
		
		// scale images
		buff1 = getScaledImage(buff1, buff1.getWidth()/10, buff1.getHeight()/10);
		buff2 = getScaledImage(buff2, buff2.getWidth()/10, buff2.getHeight()/10);
		buff3 = getScaledImage(buff3, buff3.getWidth()/10, buff3.getHeight()/10);
		
		bottom = new Container();
		bottom.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 5));

		// select img 1
		ImageIcon icon1 = new ImageIcon(buff1);
		img1 = new JButton(icon1);
		img1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImgPanel.img1Bool = true;
				ImgPanel.img2Bool = false;
				ImgPanel.img3Bool = false;
				currentImg = "Image 1";
			}
		});

		// select img 2
		ImageIcon icon2 = new ImageIcon(buff2);
		img2 = new JButton(icon2);
		img2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImgPanel.img2Bool = true;
				ImgPanel.img1Bool = false;
				ImgPanel.img3Bool = false;
				currentImg = "Image 2";
			}
		});

		// select img 3
		ImageIcon icon3 = new ImageIcon(buff3);
		img3 = new JButton(icon3);
		img3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImgPanel.img3Bool = true;
				ImgPanel.img2Bool = false;
				ImgPanel.img1Bool = false;
				currentImg = "Image 3";
			}
		});

		// button to intiate morphing
		morphButton = new JButton("Morph");
		morphButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImgPanel.doMorph = true;
			}
		});

		//add all buttons
		top = new Container();
		top.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
		top.add(img1);
		top.add(img2);
		top.add(img3);
		top.add(morphButton);
	}

	public void update(ImgPanel p) {

		//toggle control panel visibility
		if (ImgPanel.showControlPanel) this.setVisible(true);
		else this.setVisible(false);
		
			if (currentImg == "Image 1") field.setText("Image 1"); //show current selected img to morph to
			else field.setText("");
			
			if (currentImg == "Image 2") field2.setText("Image 2");
			else field2.setText("");
			
			if (currentImg == "Image 3") field3.setText("Image 3");
			else field3.setText("");
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