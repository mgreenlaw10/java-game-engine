package src.gui;

import javax.swing.JCheckBox;
import javax.swing.ImageIcon;
import src.obj.TileSet;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Graphics;
import src.obj.ui.Drawable;

public class CheckBox extends JCheckBox {

	public static Font defaultFont;

	boolean useImages = false;
	Drawable defaultImage;
	Drawable pressedImage;
	Drawable hoveredImage;

	public CheckBox() {
		super();
		clearFormat();
		if (defaultFont != null)
			setFont(defaultFont);
	}

	public CheckBox(Drawable defaultImage, Drawable pressedImage, Drawable hoveredImage) {
		super();
		clearFormat();
		if (defaultFont != null)
			setFont(defaultFont);

		this.useImages = true;
		this.defaultImage = defaultImage;
		this.pressedImage = pressedImage;
		this.hoveredImage = hoveredImage;
	}

	void clearFormat() {
		setBorderPainted		(false); // no border
		setContentAreaFilled	(false); // no background
		setFocusPainted			(false); // no focus ring
		setOpaque				(false); // let parent background show through
		setMargin(new Insets(0, -2, 0, 0)); // idk why this works
		setIconTextGap(0);
	}

	public void setIcons(TileSet icons) {
		setIcon(			(new ImageIcon(icons.getFrame(0, 0))));
		setSelectedIcon(	(new ImageIcon(icons.getFrame(0, 1))));
		setPressedIcon(		(new ImageIcon(icons.getFrame(0, 2))));
	}

	public void setIconsHorizontal(TileSet icons) {
		setIcon 				(new ImageIcon(icons.getFrame(0, 0)));
		setSelectedIcon 	    (new ImageIcon(icons.getFrame(1, 0)));
		setRolloverIcon 		(new ImageIcon(icons.getFrame(2, 0)));
	}

	public int right() {
		return getX() + getWidth();
	}

	public int bottom() {
		return getY() + getHeight();
	}

	@Override
	public void paintComponent(Graphics g) {
		if (useImages) {
			if (getModel().isSelected()) {
	        	g.drawImage(pressedImage.getImage(), 0, 0, getWidth(), getHeight(), null);
			}
	    	else if (getModel().isRollover()) {
	        	g.drawImage(hoveredImage.getImage(), 0, 0, getWidth(), getHeight(), null);
	    	}
	    	else {
	        	g.drawImage(defaultImage.getImage(), 0, 0, getWidth(), getHeight(), null);
	    	}
		}
		else {
			super.paintComponent(g);
		}
	}
}