package src.gui;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import src.obj.NinePatchTexture;

import javax.swing.JTextField;
import src.program.editor.Editor;
import java.awt.Insets;
import src.math.Vec2i;

public class TextField extends JTextField {

	public static Font defaultFont;
	public Vec2i offset; // text offset

	NinePatchTexture background = Editor.TEXTFIELD_BG_TEXTURE;

	public TextField(int cols) {
		super(cols);
		offset = new Vec2i();
		clearFormat();
		setOpaque(false);
		if (defaultFont != null)
			setFont(defaultFont);
		repaint();
	}

	public TextField() {
		super();
		offset = new Vec2i();
		clearFormat();
		setOpaque(false);
		if (defaultFont != null)
			setFont(defaultFont);
		repaint();
	}

	public void clearFormat() {
		setBorder(null);
		setFocusable(true);
		setMargin(new Insets(0, 4, 0, 0));
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

		g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
		
		g.translate(offset.x, offset.y);
		super.paintComponent(g);
		g.translate(-offset.x, -offset.y);
	}
}