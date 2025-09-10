package src.math;

public class Vec2d implements java.io.Serializable {

    public double x;
    public double y;
    
    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d(Vec2i v) {
        this.x = v.x;
        this.y = v.y;
    }
 
    public Vec2d() {
        this.x = 0;
        this.y = 0;
    }

    public double magnitude() {
        if (this.x == 0 && this.y == 0) 
            return 0;
        else if (this.x == 0) 
            return Math.abs(y);
        else if (this.y == 0) 
            return Math.abs(x);
        return Math.hypot(x, y);
    }
    
    public Vec2d normal() {
        Vec2d out = new Vec2d(x, y);
        out.normalize();
        return out;
    }

    public static double Dot(Vec2d v1, Vec2d v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public void zero() {
        x = 0;
        y = 0;
    }

    public static Vec2d VectorSum(Vec2d v1, Vec2d v2) {
        return new Vec2d(v1.x + v2.x, v1.y + v2.y);
    }
    
    public static Vec2d VectorScale(Vec2d v, double scalar) {
        return new Vec2d(v.x * scalar, v.y * scalar);
    }
    
    /**
     * @param v: The target direction
     * 
     * @return a normal Vec2d pointing from this to @param v.
     */
    public Vec2d normalTowards(Vec2d v) {
        Vec2d out = new Vec2d(v.x - this.x, v.y - this.y);
        out.normalize();
        return out;
    }
    
    /**
     * @param v: The Vec2d to compare to this
     * 
     * @return the distance between this and @param v.
     */
    public double distanceFrom(Vec2d v) {
        Vec2d tmp = new Vec2d(v.x - this.x, v.y - this.y);
        return tmp.magnitude();
    }
    
    public void add(Vec2d v) {
        this.x += v.x;
        this.y += v.y;
    }

    public Vec2d sub(Vec2d v) {
        return new Vec2d(this.x - v.x, this.y - v.y);
    }
    
    public void scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }
  
    public void normalize() {
        if (x == 0 && y == 0) 
            return;
        double mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        this.x /= mag;
        this.y /= mag;
    }
    
    /**
     * Rotate this Vec2d counterclockwise in place by @param radians.
     * 
     * @param radians: The number of radians to rotate by
    */
    public void rotate(double radians) {
        this.x *= Math.cos(radians) - y * Math.sin(radians);
        this.y *= Math.sin(radians) + y * Math.cos(radians);
    }
 
    public void flip() {
        this.x *= -1;
        this.y *= -1;
    }
    
    /**
     * Randomly change the components of a Vec2d such that
     * the new direction within PI radians of the original direction.
     */
    public void wiggle() {
        int s = (Math.random() >= 0.5) ? 1 : -1;
        rotate(Math.PI * Math.random() * s);
    }
  
    public static Vec2d CreateNormal(double x, double y) {
        return new Vec2d(x, y).normal();
    }
    
    /**
     * @return a normal Vec2d pointing in a random direction.
     */
    public static Vec2d RandomNormal() {
        int s1 = (Math.random() >= 0.5) ? 1 : -1;
        int s2 = (Math.random() >= 0.5) ? 1 : -1;
     
        return new Vec2d(Math.random() * s1, Math.random() * s2).normal();
    }
  
    @Override
    public String toString() { return String.format("(%.2f,%.2f)", x, y); }

    @Override public Vec2d clone() {
        return new Vec2d(x, y);
    }

    @Override
    public int hashCode() {
        return (int)(x * y * 67 - 67);
    }
}