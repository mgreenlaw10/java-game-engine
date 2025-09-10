package src.program.game;

import src.math.Vec2i;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.util.ArrayList;

import src.obj.TileSet;
import src.math.Vec2i;

public class AnimationPlayer {
    
    record AnimationData(String name, TileSet tileSet, Vec2i[] frameOrder, double delay, boolean hFlip) {}
    ArrayList<AnimationData> animations;

    AnimationData lastAnimation;
    AnimationData activeAnimation;
    long startTime = 0;

    boolean looping = false;
    boolean locked = false;

    public class AnimationFuture {

        AnimationData animation;
        long startTime;
        public AnimationFuture(AnimationData animation, long startTime) {
            this.animation = animation;
            this.startTime = startTime;
        }
        public boolean isFinished() {
            long dt = (System.nanoTime() - startTime) / (long)Math.pow(10, 6);
            return (dt > animation.delay() * animation.frameOrder().length);
        }
    }
    
    public AnimationPlayer() {
        animations = new ArrayList<>();
    }
    
    public void addAnimation(String name, TileSet tileSet, Vec2i[] frameOrder, double delay, boolean hFlip) { // ms delay
        // possible conflict if two animations have the same name 
        animations.add(new AnimationData(name, tileSet, frameOrder, delay, hFlip));
    }

    // begin an animation once or in an infinite loop
    public AnimationFuture playAnimation(String name, boolean looping) {
        // if locked, return
        if (locked)
            return null;
        // if this animation is already playing, return
        if (activeAnimation != null && activeAnimation.name().equals(name))
            return null;
        // if not looping, save previous animation/image to return to
        if (!looping) {
            lastAnimation = activeAnimation;
        } else {
            lastAnimation = null;
        }
        this.looping = looping;
        for (var anim : animations) {
            if (anim.name().equals(name)) {  
                activeAnimation = anim;
                startTime = System.nanoTime();
            }
        }
        return new AnimationFuture(activeAnimation, startTime);
    }

    // lock this animation in place until unlock() is called
    public AnimationFuture playAndLock(String name) {
        AnimationFuture ret = playAnimation(name, true);
        locked = true;
        return ret;
    }
    // play once uninterrupted
    public AnimationFuture playOnceUninterrupted(String name) {
        AnimationFuture ret = playAnimation(name, false);
        locked = true;
        return ret;
    }
    public void unlock() {
        locked = false;
    }
    
    // get the frame of the active animation at this point in time
    public BufferedImage getFrame() {

        long dt = (System.nanoTime() - startTime) / (long)Math.pow(10, 6);
        int i = (int)(dt / activeAnimation.delay());
        Vec2i[] frames = activeAnimation.frameOrder();

        if (!looping && i >= frames.length) {
            activeAnimation = lastAnimation;
            unlock();
            // it is implied that if {activeAnimation} exists, it should be looping
            looping = (activeAnimation != null);      
        }

        BufferedImage result = activeAnimation.tileSet()
                                        .getFrame(frames[i % frames.length].x, frames[i % frames.length].y);

        if (activeAnimation.hFlip())
            return xFlipImage(result);
        // if (activeAnimation.vFlip())
        //     return yFlipImage(result);
        return result;
    }

    // flip an image horizontally
    BufferedImage xFlipImage(BufferedImage img) {
        var result = new BufferedImage 
        (
            img.getWidth(),
            img.getHeight(),
            img.getType()
        );
        ((Graphics2D)result.createGraphics())
            .drawImage(img, img.getWidth(), 0, -img.getWidth(), img.getHeight(), null);

        return result;
    }

    // flip an image vertically
    BufferedImage yFlipImage(BufferedImage img) {
        var result = new BufferedImage 
        (
            img.getWidth(),
            img.getHeight(),
            img.getType()
        );
        ((Graphics2D)result.createGraphics())
            .drawImage(img, 0, img.getHeight(), img.getWidth(), -img.getHeight(), null);

        return result;
    }

    // whether or not {name} is the active animation
    public boolean isPlaying(String name) {
        return activeAnimation != null && activeAnimation.name().equals(name);
    }

    // returns a frame order for row {row}
    public Vec2i[] rowFrames(int row, int numCols) {
        Vec2i[] result = new Vec2i[numCols];
        for (int i = 0; i < numCols; i++) {
            result[i] = new Vec2i(i, row);
        }
        return result;
    }

    // returns a frame order for col {col}
    public Vec2i[] colFrames(int col, int numRows) {
        Vec2i[] result = new Vec2i[numRows];
        for (int i = 0; i < numRows; i++) {
            result[i] = new Vec2i(col, i);
        }
        return result;
    }

    public ArrayList<AnimationData> getAnimations() {
        return animations;
    }

    public AnimationData getActiveAnimation() {
        return activeAnimation;
    }
}
