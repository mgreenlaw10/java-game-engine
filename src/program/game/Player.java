package src.program.game;

import java.awt.image.BufferedImage;
import src.math.*;
import src.obj.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.BitSet;
import java.util.ArrayList;

import src.program.game.ui.KeyFrameListener;
import src.program.game.AnimationPlayer.KeyFrameEvent;

import src.obj.Direction;

public class Player extends Entity implements KeyFrameListener {

	TileSet runAnimation;
	TileSet idleAnimation;
	TileSet attackAnimation;
	TileSet hurtAnimation;
	final int KEYFRAME_TRIGGER_ATTACK = 2;
	PlayerInputController input;
	// attacking
	Box2d attackBounds;

	public Player(Vec2d spawnPoint, Vec2d size, Game gameHandle) {
		super(spawnPoint, size, gameHandle);
		input = new PlayerInputController();
		speed = 200;
		mass = 100;
		loadResources();
		animationPlayer.addKeyFrameListener(this);
		animationPlayer.playAnimation("idle_right", true);
		attackBounds = null;
	}

	void loadResources() {
		idleAnimation = new TileSet("res/tileset/idle.png", 16, 16, 32, 32, 64, 64);
		runAnimation = new TileSet("res/tileset/run.png", 16, 16, 32, 32, 64, 64);
		attackAnimation = new TileSet("res/tileset/sword.png", 48, 48, 16, 16, 32, 32);
		hurtAnimation = new TileSet("res/tileset/hurt.png", 16, 16, 32, 32, 64, 64);

		animationPlayer.addAnimation("walk_right", runAnimation, animationPlayer.rowFrames(0, runAnimation.cols), 200, null, false);
		animationPlayer.addAnimation("walk_left", runAnimation, animationPlayer.rowFrames(0, runAnimation.cols), 200, null, true);
		animationPlayer.addAnimation("walk_down", runAnimation, animationPlayer.rowFrames(1, runAnimation.cols), 200, null, false);
		animationPlayer.addAnimation("walk_up", runAnimation, animationPlayer.rowFrames(2, runAnimation.cols), 200, null, false);

		animationPlayer.addAnimation("idle_right", idleAnimation, animationPlayer.rowFrames(0, idleAnimation.cols), 200, null, false);
		animationPlayer.addAnimation("idle_left", idleAnimation, animationPlayer.rowFrames(0, idleAnimation.cols), 200, null, true);
		animationPlayer.addAnimation("idle_down", idleAnimation, animationPlayer.rowFrames(1, idleAnimation.cols), 200, null, false);
		animationPlayer.addAnimation("idle_up", idleAnimation, animationPlayer.rowFrames(2, idleAnimation.cols), 200, null, false);

		int[] ATTACK_KEYFRAMES = new int[] {KEYFRAME_TRIGGER_ATTACK};

		animationPlayer.addAnimation("attack_right", attackAnimation, animationPlayer.rowFrames(0, 5), 80, ATTACK_KEYFRAMES, false);
		animationPlayer.addAnimation("attack_left", attackAnimation, animationPlayer.rowFrames(0, 5), 80, ATTACK_KEYFRAMES, true);
		animationPlayer.addAnimation("attack_down", attackAnimation, animationPlayer.rowFrames(1, 5), 80, ATTACK_KEYFRAMES, false);
		animationPlayer.addAnimation("attack_up", attackAnimation, animationPlayer.rowFrames(2, 5), 80, ATTACK_KEYFRAMES, false);

		animationPlayer.addAnimation("hurt_right", hurtAnimation, animationPlayer.rowFrames(0, 4), 80, null, false);
		animationPlayer.addAnimation("hurt_left", hurtAnimation, animationPlayer.rowFrames(0, 4), 80, null, true);
		animationPlayer.addAnimation("hurt_down", hurtAnimation, animationPlayer.rowFrames(1, 4), 80, null, false);
		animationPlayer.addAnimation("hurt_up", hurtAnimation, animationPlayer.rowFrames(2, 4), 80, null, false);

		String[] ATTACK_ANIMATIONS = new String[] {"attack_right", "attack_left", "attack_down", "attack_up"};
		String[] HURT_ANIMATIONS = new String[] {"hurt_right", "hurt_left", "hurt_down", "hurt_up"};
		String[] RIGHT_ANIMATIONS = new String[] {"walk_right", "idle_right", "attack_right", "hurt_right"};
		String[] LEFT_ANIMATIONS = new String[] {"walk_left", "idle_left", "attack_left", "hurt_left"};
		String[] DOWN_ANIMATIONS = new String[] {"walk_down", "idle_down", "attack_down", "hurt_down"};
		String[] UP_ANIMATIONS = new String[] {"walk_up", "idle_up", "attack_up", "hurt_up"};

		animationPlayer.addAnimationClass("attack", ATTACK_ANIMATIONS);
		animationPlayer.addAnimationClass("hurt", HURT_ANIMATIONS);
		animationPlayer.addAnimationClass("right", RIGHT_ANIMATIONS);
		animationPlayer.addAnimationClass("left", LEFT_ANIMATIONS);
		animationPlayer.addAnimationClass("down", DOWN_ANIMATIONS);
		animationPlayer.addAnimationClass("up", UP_ANIMATIONS);
	}

	public void update(double delta) {
		handleTeleporterCollision();
		handleMovementInput();
		animationPlayer.findKeyFrameUpdates();
		if (attacking) {
			attack();
		}
		if (triggerAttack && attackBounds != null) {
			// clone, don't get original list
			var entitiesHit = new ArrayList<>(gameHandle.getLevelManager().getCurrentLevel().getEntitiesInside(attackBounds));
			for (Entity e : entitiesHit) {
				if (e != this) e.kill();
			}
			attackBounds = null;
			triggerAttack = false;
		}
	}

	boolean teleported = false;
	boolean tpCooldown = false;
	void handleTeleporterCollision() {
		for (Teleporter tp : gameHandle.getLevelManager().getCurrentLevel().teleporters) {
			Box2d tpBounds = new Box2d (
				tp.mapCoords.x * gameHandle.getTileSize(),
				tp.mapCoords.y * gameHandle.getTileSize(),
				gameHandle.getTileSize(),
				gameHandle.getTileSize()
			);
			if (this.intersects(tpBounds)) {
				teleported = true;
				if (!tpCooldown) {
					tp.onPlayerCollision();
					tpCooldown = true;
				}
			}
		}
		if (!teleported) {
			tpCooldown = false;
		}
		teleported = false;
	}

	void handleMovementInput() {
		if (input.upPressed()) {
			animationPlayer.playAndLock("walk_up");
			velocity.y -= speed;
		} 
		else if (animationPlayer.isPlaying("walk_up")) {
			animationPlayer.unlock();
		}
		if (input.downPressed()) {
			animationPlayer.playAndLock("walk_down");
			velocity.y += speed;
		}
		else if (animationPlayer.isPlaying("walk_down")) {
			animationPlayer.unlock();
		}
		if (input.leftPressed()) {
			animationPlayer.playAndLock("walk_left");
			velocity.x -= speed;
		}
		else if (animationPlayer.isPlaying("walk_left")) {
			animationPlayer.unlock();
		}
		if (input.rightPressed()) {
			animationPlayer.playAndLock("walk_right");
			velocity.x += speed;
		}
		else if (animationPlayer.isPlaying("walk_right")) {
			animationPlayer.unlock();
		}
		if (!input.moving()) {
			if (animationPlayer.isPlayingClass("right")) {
				animationPlayer.playAnimation("idle_right", true);
			} else if (animationPlayer.isPlayingClass("left")) {
				animationPlayer.playAnimation("idle_left", true);
			} else if (animationPlayer.isPlayingClass("up")) {
				animationPlayer.playAnimation("idle_up", true);
			} else if (animationPlayer.isPlayingClass("down")) {
				animationPlayer.playAnimation("idle_down", true);
			}	
		}
	}

	boolean attacking = false;
	boolean lockAttack = false;
	// when the actual damage is done
	boolean triggerAttack = false;
	public void attack() {
		animationPlayer.unlock();

		if (lockAttack)
			return;
		lockAttack = true;

		// create a hurtbox in front of the player's direction
		attackBounds = ((Box2d)(this)).clone();
		if (input.moving() ) {
			if (animationPlayer.isPlayingClass("up")) {
				animationPlayer.playOnceUninterrupted("attack_up");
				attackBounds.y -= height;
			} else if (animationPlayer.isPlayingClass("down")) {
				animationPlayer.playOnceUninterrupted("attack_down");
				attackBounds.y += height;
			} else if (animationPlayer.isPlayingClass("left")) {
				animationPlayer.playOnceUninterrupted("attack_left");
				attackBounds.x -= width;
			} else if (animationPlayer.isPlayingClass("right")) {
				animationPlayer.playOnceUninterrupted("attack_right");
				attackBounds.x += width;
			}
		} else {
			switch (input.getLastPressedMovementKey()) {	
				case PlayerInputController.INPUT_UP -> {
					animationPlayer.playOnceUninterrupted("attack_up");
					attackBounds.y -= height;
				}
				case PlayerInputController.INPUT_DOWN -> {
					animationPlayer.playOnceUninterrupted("attack_down");
					attackBounds.y += height;
				}
				case PlayerInputController.INPUT_LEFT -> {
					animationPlayer.playOnceUninterrupted("attack_left");
					attackBounds.x -= width;
				}
				case PlayerInputController.INPUT_RIGHT -> {
					animationPlayer.playOnceUninterrupted("attack_right");
					attackBounds.x += width;
				}
			}
		}
		lockAttack = false;
	}

	// when the player is damaged by an entity
	public void onDamaged(int damage) {
		if (animationPlayer.isPlayingClass("up")) {
			animationPlayer.playOnceUninterrupted("hurt_up");
		} else if (animationPlayer.isPlayingClass("down")) {
			animationPlayer.playOnceUninterrupted("hurt_down");
		} else if (animationPlayer.isPlayingClass("left")) {
			animationPlayer.playOnceUninterrupted("hurt_left");
		} else if (animationPlayer.isPlayingClass("right")) {
			animationPlayer.playOnceUninterrupted("hurt_right");
		}
	}

	public void setAttacking(boolean val) {
		attacking = val;
	}

    public PlayerInputController getPlayerInputController() {
		return input;
	}

	@Override
	public void whileDying() {

	}

	@Override
	public String getStateString() {
		if (animationPlayer.isPlayingClass("attack")) {
			return "ATTACKING";
		}
		if (animationPlayer.isPlayingClass("hurt")) {
			return "HURTING";
		}
		if (input.moving()) {
			return "MOVING";
		} else {
			return "IDLE";
		}
	}

	@Override
	public void keyFrameReached(KeyFrameEvent keyFrame) {
		// if any attack animation is playing and the keyframe has been reached
		if (animationPlayer.isOfClass("attack", keyFrame.animation()) && keyFrame.frame() == KEYFRAME_TRIGGER_ATTACK) {
			triggerAttack = true;
		}
	}
}