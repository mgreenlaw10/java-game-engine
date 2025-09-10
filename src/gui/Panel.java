package src.gui;

import java.util.HashMap;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import src.Renderer;
import src.math.Box2i;
import src.obj.Texture;
import src.math.Vec2i;
import java.awt.Dimension;

import src.obj.ui.Drawable;

public class Panel extends Renderer {

	record ComponentData(Rectangle bounds) {}

	HashMap<Component, ComponentData> data;
	BufferedImage background;

	Box2i preferredBounds;
	Vec2i preferredSize;

	public Panel() {

		super();
		this.data = new HashMap<>();
		setLayout(null);     
        setOpaque(false);
        setFocusable(true);
        defineRenderMethod(this::draw);
	}

	public void setPreferredBounds(int x, int y, int w, int h) {
		preferredBounds = new Box2i(x, y, w, h);
	}

	// remove this function
	@Override
	public void setPreferredSize(Dimension d) { 
		System.err.println("DO NOT CALL THIS FUNCTION");
		return; 
	}
	// use this instead
	public void setPreferredSize(int w, int h) {
		preferredSize = new Vec2i(w, h);
		// needed for components that redraw based on dimensions
		setBounds(new Rectangle(getX(), getY(), w, h));
	}

	public void addComponent(Panel p) {
		if (p.preferredBounds == null)
			return;

		data.put(p, new ComponentData(p.preferredBounds));
		p.setBounds(p.preferredBounds);
		add(p);
	}

	public void addComponent(Component c, int x, int y, int w, int h) {

		if (!data.keySet().contains(c)) {
			var bounds = new Rectangle(x, y, w, h);
			data.put(c, new ComponentData(bounds));
			c.setBounds(bounds);
			add(c);
		}
	}

	public void addComponent(Panel p, int x, int y) {
		if (p.preferredSize == null)
			return;

		var bounds = new Rectangle (
			x, y, 
			p.preferredSize.x, p.preferredSize.y
		);
		data.put(p, new ComponentData(bounds));
		p.setBounds(bounds);
		add(p);
	}

	public void removeComponent(Component c) {

		if (data.keySet().contains(c)) {
			data.remove(c);
			remove(c);
		}
	}

	public void setBG(Drawable drawable) {
		background = drawable.getImage();
	}
	public void setBG(Color color) {

		int w = getWidth();
		int h = getHeight();

		var bg = new BufferedImage(w, h, TYPE_INT_ARGB);
		var g2 = (Graphics2D) bg.createGraphics();

		g2.setColor(color);
		g2.fillRect(0, 0, w, h);
		background = bg;
	}

	public void draw(Graphics2D g2) {
		
    	if (background != null) {
    		g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    	}
	}

	public int right() {
		return getX() + getWidth();
	}

	public int bottom() {
		return getY() + getHeight();
	}
}