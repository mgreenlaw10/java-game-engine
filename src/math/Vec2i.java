package src.math;

public class Vec2i implements java.io.Serializable {

	public int x;
	public int y;

	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vec2i(Vec2d v) {
		this.x = (int)v.x;
		this.y = (int)v.y;
	}

	public Vec2i() {
		this(0, 0);
	}

	/* ADD
	*/
	public void add(Vec2i v) {
		this.x += v.x;
		this.y += v.y;
	}

	public static Vec2i sum(Vec2i v0, Vec2i v1) {
		return new Vec2i(v0.x + v1.x, v0.y + v1.y);
	}

	/* SUBTRACT
	*/
	public void subtract(Vec2i v) {
		this.x -= v.x;
		this.y -= v.y;
	}

	public static Vec2i difference(Vec2i v0, Vec2i v1) {
		return new Vec2i(v0.x - v1.x, v0.y - v1.y);
	}

	/* SCALAR MULTIPLY
	*/
	public void scale(float val) { 
		this.x *= val;
		this.y *= val;
	}

	public static Vec2i scaled(Vec2i v, float scalar) {
		return new Vec2i((int)(v.x * scalar), (int)(v.y * scalar));
	}

	/* DOT
	*/
	public int dot(Vec2i v) {
		return (this.x * v.x) + (this.y * v.y);
	}

	/* TOSTRING
	*/
	@Override
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}

	@Override
	public int hashCode() {
		return x * y * 67 - 67;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Vec2i))
			return false;

		return ((Vec2i)obj).x == this.x &&
			   ((Vec2i)obj).y == this.y;
	}

	@Override
	public Vec2i clone() {

		return new Vec2i(this.x, this.y);
	}
}