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
import src.program.game.AnimationPlayer.AnimationFuture;

import src.program.game.event.GameEvent;
import src.program.game.event.FutureEvents;
import src.program.game.event.FutureEventHandle;
import src.program.game.event.LockedEventHandle;
import src.program.game.event.RecurringEventHandle;

import src.obj.Direction;

public class Player extends Entity implements KeyFrameListener {

	TileSet runAnimation;
	TileSet idleAnimation;
	TileSet attackAnimation;
	TileSet hurtAnimation;
	TileSet deathAnimation;
	final int KEYFRAME_TRIGGER_ATTACK = 2;
	PlayerInputController input;
	double attackDamage;

	public Player(Vec2d spawnPoint, Vec2d size, Game gameHandle) {
		super(spawnPoint, size, gameHandle);
		input = new PlayerInputController();
		speed = 200;
		mass = 100;
		health = 5;
		attackDamage = 1;
		loadResources();
		animationPlayer.addKeyFrameListener(this);
		animationPlayer.playAnimation("idle_right", true);
		attackBounds = null;
		showHealthBar = true;
	}

	void loadResources() {
		idleAnimation = new TileSet("res/tileset/idle.png", 16, 16, 32, 32, 64, 64);
		runAnimation = new TileSet("res/tileset/run.png", 16, 16, 32, 32, 64, 64);
		attackAnimation = new TileSet("res/tileset/sword.png", 48, 48, 16, 16, 32, 32);
		hurtAnimation = new TileSet("res/tileset/hurt.png", 16, 16, 32, 32, 64, 64);
		deathAnimation = new TileSet("res/tileset/death.png", 16, 16, 32, 32, 64, 64);

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

		animationPlayer.addAnimation("death_right", deathAnimation, animationPlayer.rowFrames(0, 4), 80, null, false);
		animationPlayer.addAnimation("death_left", deathAnimation, animationPlayer.rowFrames(0, 4), 80, null, true);
		animationPlayer.addAnimation("death_down", deathAnimation, animationPlayer.rowFrames(1, 4), 80, null, false);
		animationPlayer.addAnimation("death_up", deathAnimation, animationPlayer.rowFrames(2, 4), 80, null, false);

		String[] ATTACK_ANIMATIONS = new String[] {"attack_right", "attack_left", "attack_down", "attack_up"};
		String[] HURT_ANIMATIONS = new String[] {"hurt_right", "hurt_left", "hurt_down", "hurt_up"};
		String[] DEATH_ANIMATIONS = new String[] {"death_right", "death_left", "death_down", "death_up"};
		String[] RIGHT_ANIMATIONS = new String[] {"walk_right", "idle_right", "attack_right", "hurt_right", "death_right"};
		String[] LEFT_ANIMATIONS = new String[] {"walk_left", "idle_left", "attack_left", "hurt_left", "death_left"};
		String[] DOWN_ANIMATIONS = new String[] {"walk_down", "idle_down", "attack_down", "hurt_down", "death_down"};
		String[] UP_ANIMATIONS = new String[] {"walk_up", "idle_up", "attack_up", "hurt_up", "death_up"};

		animationPlayer.addAnimationClass("attack", ATTACK_ANIMATIONS);
		animationPlayer.addAnimationClass("hurt", HURT_ANIMATIONS);
		animationPlayer.addAnimationClass("death", DEATH_ANIMATIONS);
		animationPlayer.addAnimationClass("right", RIGHT_ANIMATIONS);
		animationPlayer.addAnimationClass("left", LEFT_ANIMATIONS);
		animationPlayer.addAnimationClass("down", DOWN_ANIMATIONS);
		animationPlayer.addAnimationClass("up", UP_ANIMATIONS);
	}

	public void update(double delta) {
		super.update(delta);
		handleTeleporterCollision();
		handleMovementInput();
		animationPlayer.findKeyFrameUpdates();
		if (health < 5)
			tryRegenHealth();
	}
	/**
	 * Health regeneration system.
	 * 
	 * 
	 * */
	int regenRate = 1;
	int regenAmount = 1;
	FutureEventHandle regenEvent;
	/**
	 * Ensure that calls don't overlap.
	 *
	 * 
	 * */
	void tryRegenHealth() {
		if (regenEvent == null || regenEvent.hasExecuted()) {
			regenEvent = FutureEvents.doLater( () -> health += regenAmount, regenRate * 2000);
		}
	}

	/**
	 * Player-Teleporter interaction.
	 * 
	 * 
	 * */
	LockedEventHandle teleportCooldown;
	Vec2d lastUsedTeleporterPos;
	/**
	 * Call in update loop.
	 * 
	 * */
	void handleTeleporterCollision() {
		for (var tp : gameHandle.getLevelManager().getCurrentLevel().teleporters) {
			/**
			 * Calculate the in-game dimensions of each teleporter.
			 * */
			int x = tp.mapCoords.x * gameHandle.getTileSize();
			int y = tp.mapCoords.y * gameHandle.getTileSize();
			int w = gameHandle.getTileSize();
			int h = gameHandle.getTileSize();
			Box2d teleporterBounds = new Box2d(x, y, w, h);
			if (this.intersects(teleporterBounds)) {
				/**
				 * After teleporting, lock for 2 seconds.
				 * */
				if (teleportCooldown == null || !teleportCooldown.isLocked()) {
					teleportCooldown = FutureEvents.lockEvent(this::handleTeleporterCollision, 2000);
					tp.onPlayerCollision();
					lastUsedTeleporterPos = teleporterBounds.center();
				}
			}
			/**
			 * If the player is at least two blocks away (middle-to-middle) 
			 * from the last teleporter it touched, it is safe to unlock.
			 * */
			boolean playerExceededSafeRange = lastUsedTeleporterPos == null || this.center().distanceFrom(lastUsedTeleporterPos) > gameHandle.getTileSize() * 2;
			if (teleportCooldown != null && playerExceededSafeRange) { 
				teleportCooldown.unlockImmediately();
				teleportCooldown = null;
			}
		}
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
	/**
	 * Attack System.
	 * 
	 * 
	 * */
	Box2d attackBounds;
	RecurringEventHandle attackEvent;
	public void attack() {
		animationPlayer.unlock();
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
	}
	/**
	 * Called by KeyboardInput. Enter an attacking state.
	 * 
	 * 
	 * */
	public void startAttacking() {
		if (attackEvent == null) {
			attackEvent = FutureEvents.startRecurringEvent(this::attack, 0, 1000);
		}
	}
	/**
	 * Called by KeyboardInput. Exit an attacking state.
	 * 
	 * 
	 * */
	public void stopAttacking() {
		if (attackEvent != null) {
			attackEvent.stop();
			attackEvent = null;
		}
	}
	/**
	 * Currently, the player only has one keyframe in his attack animation.
	 * 
	 * */
	@Override
	public void keyFrameReached(KeyFrameEvent keyFrame) {
		if (animationPlayer.isOfClass("attack", keyFrame.animation()) && 
			keyFrame.frame() == KEYFRAME_TRIGGER_ATTACK && 
			attackBounds != null
		) {
			/**
			 * Important to clone the list.
			 * */
			var entitiesHit = new ArrayList<>(gameHandle.getLevelManager().getCurrentLevel().getEntitiesInside(attackBounds));
			for (Entity e : entitiesHit) {
				if (e != this) e.damage(attackDamage);
			}
			attackBounds = null;
		}
	}

    public PlayerInputController getPlayerInputController() {
		return input;
	}

	boolean firstPass = true;
	AnimationFuture deathAnimState;
	@Override
	protected void whileDying() {
		if (firstPass) {
			animationPlayer.unlock();
			if (animationPlayer.isPlayingClass("up")) {
				deathAnimState = animationPlayer.playOnceUninterrupted("death_up");
			} else if (animationPlayer.isPlayingClass("down")) {
				deathAnimState = animationPlayer.playOnceUninterrupted("death_down");
			} else if (animationPlayer.isPlayingClass("left")) {
				deathAnimState = animationPlayer.playOnceUninterrupted("death_left");
			} else if (animationPlayer.isPlayingClass("right")) {
				deathAnimState = animationPlayer.playOnceUninterrupted("death_right");
			}
			firstPass = false;
		}
		if (deathAnimState != null && deathAnimState.isFinished()) {
			lifeState = LifeState.DEAD;
		}
	}

	@Override
	protected void onDamaged(double damage) {
		if (animationPlayer.isPlayingClass("up")) {
			animationPlayer.playOnceUninterrupted("hurt_up");
		} else if (animationPlayer.isPlayingClass("down")) {
			animationPlayer.playOnceUninterrupted("hurt_down");
		} else if (animationPlayer.isPlayingClass("left")) {
			animationPlayer.playOnceUninterrupted("hurt_left");
		} else if (animationPlayer.isPlayingClass("right")) {
			animationPlayer.playOnceUninterrupted("hurt_right");
		}
		health -= damage;
		if (health <= 0) {
			kill();
		}
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

	

	public double getAttackDamage() {
		return attackDamage;
	}
}