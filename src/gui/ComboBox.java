package src.gui;

import javax.swing.JComboBox;
import java.awt.Font;
import java.awt.Graphics;
import src.obj.NinePatchTexture;
import src.program.editor.Editor;

public class ComboBox<T> extends JComboBox<T> {

	public static Font defaultFont;

	NinePatchTexture background = Editor.TEXTFIELD_BG_TEXTURE;

	public ComboBox(int w, int h) {
		super();
		//setPreferredSize(w, h);
		background.setSize(w, h);
		initSettings();
		if (defaultFont != null)
			setFont(defaultFont);
		repaint();
	}
	
	public ComboBox(int w, int h, T[] items) {
		super(items);
		//setPreferredSize(w, h);
		background.setSize(w, h);
		initSettings();
		if (defaultFont != null)
			setFont(defaultFont);
		repaint();
	}

	public ComboBox(T[] items) {
		this(1, 1, items);
	}

	void initSettings() {
		setBorder(null);
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

		background.setSize(getWidth(), getHeight());
		g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
		super.paintComponent(g);
	}
}