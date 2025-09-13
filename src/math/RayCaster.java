package src.math;

import src.program.game.Entity;
import java.util.ArrayList;

public class RayCaster {
    /**
     * RayCaster has static methods that implement
     * basic ray casting functionality.
     * 
     */
    /**
     * Rays added to this queue during an update cycle
     * will be drawn by the camera in the next frame.
     */
    private static final ArrayList<Ray2d> drawQueue = new ArrayList<>();
    
    /* ****************************************
     *               RAY STRUCT
     * ****************************************
     */
    public static class Ray2d {
        /**
        * A ray has an origin point and a normal vector
        * that points in a particular direction.
        * 
        * The magnitude of a ray is inherently 1 because
        * the direction is a normal vector.
        */
       public Vec2d origin;
       public Vec2d dir;

       /**
        * @param x1: The x coordinate of the origin
        * @param y1: The y coordinate of the origin
        * @param x2: The x component of the dir
        * @param y2: The y component of the dir
        */
       public Ray2d(int x1, int y1, int x2, int y2) {
           this.origin = new Vec2d(x1, y1);
           this.dir = Vec2d.CreateNormal(x2, y2);
       }

       /**
        * @param origin: A Vec2d representing the origin point of this ray
        * @param dir: A Vec2d representing the dir of this ray
        */
       public Ray2d(Vec2d origin, Vec2d dir) {
           this.origin = origin;
           this.dir = dir.normal();
       }
    }
    
    /* ****************************************
     *                RAY SIM
     * ****************************************/
    /**
     * @param origin: The origin of the Ray2d to cast.
     *
     * @param dir: The direction of the Ray2d to cast.
     *
     * @param target: The Box2d to test for collision.
     * 
     * @param obstacles: Any list of Box2d that would logically block
                         a ray from reaching the target. Can be NULL.
     *
     * @param len: The max length of the ray. Can be Math.INFINITY.
     * 
     * @param QUERY_endpoint: A Vec2d buffer to retrieve the nearest point
     *                        of collision with ANY passed Box2f. Can be NULL,
     *                        and will return as NULL if the ray does not intersect
     *                        any Box2d.
     * 
     * @param draw: Whether or not to add this ray to the draw queue.
     * 
     * @return whether or not a Ray2d cast from @param origin in the direction @param dir
     *         with max length @param len will hit @param target without first hitting any
     *         Box2d in @param obstacles.
     */
    public static boolean QueryRaySim(  final Vec2d origin, 
                                        final Vec2d dir, 
                                        final Box2d target, 
                                        final Box2d[] obstacles, 
                                        final double len, 
                                        Vec2d QUERY_endpoint,
                                        boolean draw  ) {
        /**
         * 
         */
        Ray2d ray = new Ray2d(origin, dir);
        boolean hitTarget = false;
        Vec2d endpoint;
        double minDst = len;
        /**
         * For all Box2d in @param obstacles, calculate the nearest 
         * point at which the ray intersects any Box2d.
         */
        for (Box2d b : obstacles) if (((endpoint = GetInitialIntersectionPoint(ray, b)) != null) && 
                                       (endpoint.magnitude() < minDst))
                                            minDst = endpoint.magnitude();
        /**
         * Calculate if the ray intersects @param target at a nearer point
         * than any Box2f in @param obstacles.
         */
        if ((endpoint = GetInitialIntersectionPoint(ray, target)) != null &&
            (endpoint.magnitude() <= minDst))
                hitTarget = true;
        /**
         * Fulfill endpoint query if it was requested.
         */
        if (QUERY_endpoint != null)
            QUERY_endpoint = endpoint;
        /**
         * Draw Ray
         */
        if (draw) {
            QueueRayDraw(ray, minDst);
        }
        
        return hitTarget;
    }
    
    /**
     * @param ray: Any Ray2d.
     * @param box: Any Box2d.
     * @return either null if @param ray does not intersect @param box, or 
     *         a Vec2d representing the ray segment between @param ray's
     *         origin and its initial point of intersection with @param box
     */
    public static Vec2d GetInitialIntersectionPoint(Ray2d ray, Box2d box) { 
        double tx1 = (box.x - ray.origin.x) / ray.dir.x;
        double tx2 = (box.x + box.width - ray.origin.x) / ray.dir.x;
        
        double ty1 = (box.y - ray.origin.y) / ray.dir.y;
        double ty2 = (box.y + box.height - ray.origin.y) / ray.dir.y;
        
        double tfx = Math.max(tx1, tx2);
        double tnx = Math.min(tx1, tx2);
        
        double tfy = Math.max(ty1, ty2);
        double tny = Math.min(ty1, ty2);
        
        double tfn = Math.max(tnx, tny);
        double tnf = Math.min(tfx, tfy);
        
        if (tfn < tnf && tfn >= 0) {
            double px = ray.dir.x * tfn;
            double py = ray.dir.y * tfn;
            return new Vec2d(px, py);
        }
        else return null;
    }
    
    /**
     * @param ray: Any Ray2d.
     * @param box: Any Box2d.
     * @param len: The max length of the ray.
     * @return whether or not @param ray intersects @param box
     * @deprecated because QueryRaySim() does this and more. It's good
     *             to keep this method here, though, because it purely
     *             demonstrates the AABB ray collision algorithm.
     */
    @Deprecated public boolean RayVBox(Ray2d ray, Box2d box, double len)
    {
        double tx1 = (box.x - ray.origin.x) / ray.dir.x;
        double tx2 = (box.x + box.width - ray.origin.x) / ray.dir.x;
        
        double ty1 = (box.y - ray.origin.y) / ray.dir.y;
        double ty2 = (box.y + box.height - ray.origin.y) / ray.dir.y;
        
        double tfx = Math.max(tx1, tx2);
        double tnx = Math.min(tx1, tx2);
        
        double tfy = Math.max(ty1, ty2);
        double tny = Math.min(ty1, ty2);
        
        double tfn = Math.max(tnx, tny);
        double tnf = Math.min(tfx, tfy);
        
        return (tfn < tnf && tfn >= 0 && tfn <= len);
    }
    
    /* ****************************************
     *                DRAW RAYS
     * ****************************************
    
     * When you want to draw a Ray2d, send it to the queue.
     * Next frame, the camera will read the queue and
     * draw each ray, then clear the queue.
     *
     *
     * 
     * Queue a Ray2d to be drawn during a draw cycle.
     * 
     * @param ray: Any Ray2d.
     * @param len: The length of the ray segment to draw.
     */
    public static void QueueRayDraw(Ray2d ray, double len) {
        ray.dir.scale(len);
        drawQueue.add(ray);
    }
    
    public static ArrayList<Ray2d> getDrawQueue() {
        return drawQueue;
    }
    
    public static void clearDrawQueue() {
        drawQueue.clear();
    }
}