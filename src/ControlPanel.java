import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

public class ControlPanel extends JPanel {
	private Container bottom, top;
	private JButton img1, img2, img3;
	private JButton morphButton;
	private JLabel label;
	private JTextField field;

	private Container center;

	public ControlPanel() {
		super();
		setBorder(new TitledBorder("Control Panel"));
		setComponentsAttributes();
		setLayout(new BorderLayout());
		add(bottom, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);


		label = new JLabel("Selected Image");
		field = new JTextField(5);
		field.setFocusable(false);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		center = new Container();
		center.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
		center.add(label);
		center.add(field);

		add(center, BorderLayout.CENTER);
	}

	private void setComponentsAttributes() {
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

		// TODO Auto-generated method stub
		bottom = new Container();
		bottom.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 5));

		ImageIcon icon1 = new ImageIcon(buff1);
		img1 = new JButton(icon1);
		img1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				ImgPanel.selectedImg = "Image 1";
				System.out.println("click");
//				System.out.println(ImgPanel.selectedImg);
				ImgPanel.img2Bool = false;
				ImgPanel.img3Bool = false;
				ImgPanel.img1Bool = true;
//				field.setText(String.format("Image 1"));
				System.out.println("Bool1: " + ImgPanel.img1Bool);
				System.out.println("Bool2: " + ImgPanel.img2Bool);
				System.out.println("Bool3: " + ImgPanel.img3Bool);
			}
		});

		ImageIcon icon2 = new ImageIcon(buff2);
		img2 = new JButton(icon2);
		img2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				ImgPanel.selectedImg = "Image 2";
//				System.out.println(ImgPanel.selectedImg);
				ImgPanel.img1Bool = false;
				ImgPanel.img3Bool = false;
				ImgPanel.img2Bool = true;
//				field.setText(String.format("Image 2"));
				System.out.println("Bool1: " + ImgPanel.img1Bool);
				System.out.println("Bool2: " + ImgPanel.img2Bool);
				System.out.println("Bool3: " + ImgPanel.img3Bool);
			}
		});

		ImageIcon icon3 = new ImageIcon(buff3);
		img3 = new JButton(icon3);
		img3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				ImgPanel.selectedImg = "Image 3";
//				System.out.println(ImgPanel.selectedImg);
				ImgPanel.img1Bool = false;
				ImgPanel.img2Bool = false;
				ImgPanel.img3Bool = true;
//				field.setText(String.format("Image 3"));
				System.out.println("Bool1: " + ImgPanel.img1Bool);
				System.out.println("Bool2: " + ImgPanel.img2Bool);
				System.out.println("Bool3: " + ImgPanel.img3Bool);
			}
		});

		morphButton = new JButton("Morph");
		morphButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!ImgPanel.doMorph) ImgPanel.doMorph = true;
				System.out.println(ImgPanel.doMorph);
			}
		});

		top = new Container();
		top.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
		top.add(img1);
		top.add(img2);
		top.add(img3);
		top.add(morphButton);
	}

	public void update(ImgPanel p) {
		if (ImgPanel.showControlPanel) this.setVisible(true);
		else this.setVisible(false);

		field.setText(String.format((String)p.selectedImg));
	}

	private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}


















