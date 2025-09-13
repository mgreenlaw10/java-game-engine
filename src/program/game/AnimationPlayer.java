package src.program.game;

import src.math.Vec2i;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import src.obj.TileSet;
import src.math.Vec2i;

import src.program.game.ui.KeyFrameListener;

public class AnimationPlayer {
    
    public record AnimationData(String name, TileSet tileSet, Vec2i[] frameOrder, double delay, int[] keyFrames, boolean hFlip) {}
    public record KeyFrameEvent(AnimationData animation, int frame) {}
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
    
    ArrayList<AnimationData> animations;
    HashMap<String, AnimationData[]> animationClasses;
    ArrayList<KeyFrameListener> keyFrameListeners;
    AnimationData lastAnimation;
    AnimationData activeAnimation;
    long startTime = 0;
    boolean looping = false;
    boolean locked = false;

    public void addKeyFrameListener(KeyFrameListener listener) {
        keyFrameListeners.add(listener);
    }
    public void activateKeyFrameListeners(int keyFrame) {
        for (KeyFrameListener listener : keyFrameListeners) {
            AnimationData clone = new AnimationData (
                activeAnimation.name(),
                activeAnimation.tileSet(),
                activeAnimation.frameOrder(),
                activeAnimation.delay(),
                activeAnimation.keyFrames(),
                activeAnimation.hFlip()
            );
            listener.keyFrameReached(new KeyFrameEvent(clone, keyFrame));
        }
    }
    
    public AnimationPlayer() {
        animations = new ArrayList<>();
        animationClasses = new HashMap<>();
        keyFrameListeners = new ArrayList<>();
    }
    
    public void addAnimation(String name, TileSet tileSet, Vec2i[] frameOrder, double delay, int[] keyFrames, boolean hFlip) { // ms delay
        // possible conflict if two animations have the same name 
        animations.add(new AnimationData(name, tileSet, frameOrder, delay, keyFrames, hFlip));
    }

    public void addAnimationClass(String className, String[] animationNames) {
        AnimationData[] buf = new AnimationData[animationNames.length];
        int i = 0;
        for (String name : animationNames) {
            AnimationData anim = getAnimation(name);
            if (anim == null) {
                System.out.println("Could not find animation " + name + " to add to animation class " + className);
                return;
            }
            buf[i++] = anim;
        }
        animationClasses.put(className, buf);
    }

    // begin an animation once or in an infinite loop
    public AnimationFuture playAnimation(String name, boolean looping) {
        // if locked, return
        if (locked)
            return null;
        // if this animation is already playing, return
        if (activeAnimation != null && activeAnimation.name().equals(name))
            return null;
        // if not looping, save previous animation to return to
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
        int frameCol = frames[i % frames.length].x;
        int frameRow = frames[i % frames.length].y;
        BufferedImage result = activeAnimation.tileSet().getFrame(frameCol, frameRow);

        if (activeAnimation.hFlip())
            return xFlipImage(result);
        // if (activeAnimation.vFlip())
        //     return yFlipImage(result);
        return result;
    }

    int lastKeyFrameReached = -1;
    public void findKeyFrameUpdates() {
        // if there is no active animation, there is no keyframe pass
        if (activeAnimation == null) {
            lastKeyFrameReached = -1;
            return;
        }
        // if the active animation has no keyframes, there is no keyframe pass
        int[] keyFrames = activeAnimation.keyFrames();
        if (keyFrames == null) {
            lastKeyFrameReached = -1;
            return;
        }
        // find the current frame and check if it's a keyframe
        long dt = (System.nanoTime() - startTime) / (long)Math.pow(10, 6);
        int currentFrame = (int)(dt / activeAnimation.delay());
        if (currentFrame != lastKeyFrameReached) {
            for (int keyFrame : keyFrames) {
                if (keyFrame == currentFrame) {
                    activateKeyFrameListeners(keyFrame);
                    // make sure that the keyframe event triggers only once per anim loop
                    lastKeyFrameReached = keyFrame;
                }
            }
        }
        // loop is finished
        if (currentFrame >= activeAnimation.frameOrder().length) {
            lastKeyFrameReached = -1;
        }
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

    // whether or not an animation from class {className} is playing currently
    public boolean isPlayingClass(String className) {
        for (AnimationData anim : animationClasses.get(className)) {
            if (anim.name().equals(activeAnimation.name()))
                return true;
        }
        return false;
    }

    // whether or not {animName} is the name of an animation in class {className}
    public boolean isOfClass(String className, String animName) {
        AnimationData[] classAnims = animationClasses.get(className);
        if (classAnims == null)
            return false;
        for (var animData : classAnims) {
            if (animName.equals(animData.name())) {
                return true;
            }
        }
        return false;
    }

    // whether or not {anim} is an animation in class {className}
    public boolean isOfClass(String className, AnimationData anim) {
        AnimationData[] classAnims = animationClasses.get(className);
        if (classAnims == null)
            return false;
        for (var animData : classAnims) {
            if (animData.name().equals(anim.name())) {
                return true;
            }
        }
        return false;
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

    public AnimationData getAnimation(String name) {
        for (AnimationData anim : animations) {
            if (anim.name.equals(name)) {
                return anim;
            }
        }
        return null;
    }

    public ArrayList<AnimationData> getAnimations() {
        return animations;
    }

    public AnimationData getActiveAnimation() {
        return activeAnimation;
    }
}
