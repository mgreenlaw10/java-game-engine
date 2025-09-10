package src.gui;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import src.obj.TileSet;
import java.awt.Dimension;
import src.program.editor.Editor;
import src.obj.NinePatchTexture;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Insets;

public class Button extends JButton {

	public static Font defaultFont;

	NinePatchTexture background = Editor.BUTTON_BG_TEXTURE;
	NinePatchTexture rollover   = Editor.BUTTON_ROLLOVER_TEXTURE;
	NinePatchTexture pressed    = Editor.BUTTON_PRESSED_TEXTURE;

	public Button(int w, int h) {
		super();

		background.setSize(w, h);
		rollover.setSize(w, h);
		pressed.setSize(w, h);

		initSettings();

		if (defaultFont != null) 
			setFont(defaultFont);
		
		repaint();
	}
	// only use if you know that this component will be resized immediately
	public Button() {
		this(1, 1);
	}

	public Button(int w, int h, String str) {
		super(str);

		background.setSize(w, h);
		rollover.setSize(w, h);
		pressed.setSize(w, h);

		initSettings();

		if (defaultFont != null) 
			setFont(defaultFont);
		
		repaint();
	}
	// only use if you know that this component will be resized immediately
	public Button(String str) {
		this(1, 1, str);
	}

	public void initSettings() {
		
		setMargin(new Insets(0, 0, 0, 0)); // no inner margin to clip text
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false); // no focus ring
		setOpaque(false);
	}


	public int right() {
		return getX() + getWidth();
	}

	public int bottom() {
		return getY() + getHeight();
	}

	@Override
	public void paintComponent(Graphics g) {

		// ok to call setSize() repeatedly
		background.setSize(getWidth(), getHeight());
		rollover.setSize(getWidth(), getHeight());
		pressed.setSize(getWidth(), getHeight());

		if (getModel().isPressed())
	        g.drawImage(pressed.getImage(), 0, 0, getWidth(), getHeight(), null);

	    else if (getModel().isRollover())
	        g.drawImage(rollover.getImage(), 0, 0, getWidth(), getHeight(), null);

	    else
	        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);

		super.paintComponent(g);
	}
}