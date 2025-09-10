package src.math;

import src.program.game.Entity;
import java.util.ArrayList;

public class Collision {
	
	public static void stopEnter(Box2d r1, final Box2d r2) {
        if (!r1.intersects(r2))
            return;

        double r1_l = r1.x;
        double r1_r = r1.x + r1.width;
        double r1_t = r1.y;
        double r1_b = r1.y + r1.height;

        double r2_l = r2.x;
        double r2_r = r2.x + r2.width;
        double r2_t = r2.y;
        double r2_b = r2.y + r2.height;

        double pd_l = r2_r - r1_l;
        double pd_r = r1_r - r2_l;
        double pd_t = r2_b - r1_t;
        double pd_b = r1_b - r2_t;

        double min_pd = pd_l;
        min_pd = Math.min(min_pd, pd_r);
        min_pd = Math.min(min_pd, pd_t);
        min_pd = Math.min(min_pd, pd_b);

        if (min_pd == pd_l) r1.x += min_pd;
        else if (min_pd == pd_r) r1.x -= min_pd;
        else if (min_pd == pd_t) r1.y += min_pd;
        else r1.y -= min_pd;
    }

    record CollisionData(Entity e1, Entity e2) {}

    public static void doEntityPhysics(ArrayList<Entity> entities, ArrayList<Box2d> walls, double delta) {
        // simulate movement without collisions
        for (Entity e : entities) {
            e.getVelocity().normalize();
            e.getVelocity().scale(e.getSpeed() * delta);
            e.setPosition(Vec2d.VectorSum(e.getPosition(), e.getVelocity()));
            e.getVelocity().zero();
        }
        // find all unique collisions
        ArrayList<CollisionData> collisions = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity e1 = entities.get(i);
                Entity e2 = entities.get(j);
                if (e1.intersects(e2)) {
                    collisions.add(new CollisionData(e1, e2));
                }
            }
        }
        // resolve all collisions
        boolean resolved = true;
        do {
            for (CollisionData c : collisions) {
                // kick the lighter entity out of the heavier one
                if (c.e1.getMass() < c.e2.getMass()) {
                    stopEnter(c.e1, c.e2);
                } else {
                    stopEnter(c.e2, c.e1);
                }
            }
            for (Entity e : entities) { 
                for (Box2d wall : walls) {
                    if (e.intersects(wall)) {
                        stopEnter(e, wall);
                        // if an entity has a wall collision, push back any other entities that are colliding
                        for (CollisionData c : collisions) {
                            if (c.e1 == e)
                                stopEnter(c.e2, c.e1);
                        }
                    }
                }
            }
        } while (!resolved);
    }
}