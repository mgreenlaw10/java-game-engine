package src.gui;

import src.program.editor.Editor;
import java.awt.Font;
import javax.swing.SwingConstants;
import src.obj.NinePatchTexture;
import src.obj.ui.Drawable;
import java.awt.Graphics;
import java.awt.Insets;
import src.math.Vec2i;

public class Label extends javax.swing.JLabel {

	public static Font defaultFont;

	public static int LEFT = 0;
	public static int CENTER = 1;

	public Vec2i offset;
	public Drawable background;

	public Label(String text) {
		super(text);
		offset = new Vec2i();
		centerAlign();
		if (defaultFont != null)
			setFont(defaultFont);
	}

	public Label(String text, int alignment) {
		super(text);
		offset = new Vec2i();
		if (alignment == LEFT)
			leftAlign();
		else if (alignment == CENTER)
			centerAlign();
		if (defaultFont != null)
			setFont(defaultFont);
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	void centerAlign() {
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

	public void leftAlign() {
		setHorizontalAlignment(SwingConstants.LEFT);
		setVerticalAlignment(SwingConstants.CENTER);
	}

	public int right() {
		return getX() + getWidth();
	}

	public int bottom() {
		return getY() + getHeight();
	}

	@Override
	public void paintComponent(Graphics g) {
		if (background != null) { 
			g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
		}
		g.translate(offset.x, offset.y);
		super.paintComponent(g);
		g.translate(-offset.x, -offset.y);
	}
}