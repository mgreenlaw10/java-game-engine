package src.math;

import java.awt.geom.Rectangle2D;

public class Box2d extends Rectangle2D.Double {

	public Box2d(double x, double y, double w, double h) {
		super(x, y, w, h);
	}

	public Box2d(Vec2d xy, Vec2d wh) {
		this(xy.x, xy.y, wh.x, wh.y);
	}

	public Box2d() {
		this(0, 0, 0, 0);
	}

	public Vec2d xy() {
		return new Vec2d(x, y);
	}

	public void setXY(Vec2d xy) {
		this.x = xy.x;
		this.y = xy.y;
	}

	public Vec2d wh() {
		return new Vec2d(width, height);
	}

	public void setWH(Vec2d wh) {
		this.width  = wh.x;
		this.height = wh.y;
	}

	public Vec2d center() {
		return new Vec2d(x + width / 2, y + height / 2);
	}

	public void centerAt(Vec2d p) {
		x = p.x - width / 2;
		y = p.y - height / 2;
	}

	@Override
	public Box2d clone() {
		return new Box2d(x, y, width, height);
	}

}