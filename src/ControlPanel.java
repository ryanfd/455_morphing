import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class ControlPanel extends JPanel {
	private Container bottom, top;
	private JButton img1, img2, img3;
	private JButton morphButton;
	
	private Container center;
	
	public ControlPanel() {
		super();
		setBorder(new TitledBorder("Control Panel"));
		setComponentsAttributes();
		setLayout(new BorderLayout());
		add(bottom, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		
		center = new Container();
		center.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
		center.add(img1);
		center.add(img2);
		center.add(img3);
		center.add(morphButton);
	}

	private void setComponentsAttributes() {
		// TODO Auto-generated method stub
		bottom = new Container();
		bottom.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 5));
		
		// create img buttons
		img1 = new JButton("Img1");
		img1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ImgPanel.selectedImg = "Img1";
			}
		});
		
		img2 = new JButton("Img2");
		img2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ImgPanel.selectedImg = "Img2";
			}
		});
		
		img3 = new JButton("Img3");
		img3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ImgPanel.selectedImg = "Img3";
			}
		});
		
		top = new Container();
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		top.add(img1);
		top.add(img2);
		top.add(img3);
	}
	
	public void update(ImgPanel p) {
		if (ImgPanel.showControlPanel) this.setVisible(true);
		else this.setVisible(false);
	}
}


















