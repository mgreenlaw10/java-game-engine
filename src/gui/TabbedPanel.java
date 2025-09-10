package src.gui;

import javax.swing.JTabbedPane;
import java.awt.Rectangle;
import java.awt.Component;
import java.util.HashMap;
import java.awt.Graphics2D;
import src.obj.ui.Drawable;

public class TabbedPanel extends JTabbedPane {

	Drawable background;

	public static final int TOP = JTabbedPane.TOP;
	public static final int BOTTOM = JTabbedPane.BOTTOM;
	public static final int RIGHT = JTabbedPane.RIGHT;
	public static final int LEFT = JTabbedPane.LEFT;

	public TabbedPanel() {
		super();
	}

	public TabbedPanel(int tabPlacement) { 
		super(tabPlacement);
	}

	public void draw(Graphics2D g2) {

    	if (background != null) {
    		g2.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
    	}
    	getSelectedComponent().repaint();
	}

	public int right() {
		return getX() + getWidth();
	}

	public int bottom() {
		return getY() + getHeight();
	}
}