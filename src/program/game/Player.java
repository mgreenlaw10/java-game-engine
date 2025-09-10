package src.program.game;

import java.awt.image.BufferedImage;
import src.math.*;
import src.obj.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.BitSet;
import java.util.ArrayList;

import src.obj.Direction;

public class Player extends Entity {

	TileSet runAnimation;
	TileSet idleAnimation;
	TileSet attackAnimation;
	PlayerInputController input;
	int xDirection;
	int yDirection;

	public Player(Vec2d spawnPoint, Vec2d size, Game gameHandle) {
		super(spawnPoint, size, gameHandle);
		input = new PlayerInputController();
		speed = 200;
		mass = 100;
		xDirection = Direction.WEST;
		yDirection = Direction.SOUTH;
		loadResources();
		// default animation
		animationPlayer.playAnimation("idle_right", true);
	}

	void loadResources() {
		idleAnimation = new TileSet("res/tileset/idle.png", 16, 16, 32, 32, 64, 64);
		runAnimation = new TileSet("res/tileset/run.png", 16, 16, 32, 32, 64, 64);
		attackAnimation = new TileSet("res/tileset/sword.png", 48, 48, 16, 16, 32, 32);

		animationPlayer.addAnimation("walk_right", runAnimation, animationPlayer.rowFrames(0, runAnimation.cols), 200, false);
		animationPlayer.addAnimation("walk_left", runAnimation, animationPlayer.rowFrames(0, runAnimation.cols), 200, true);
		animationPlayer.addAnimation("walk_down", runAnimation, animationPlayer.rowFrames(1, runAnimation.cols), 200, false);
		animationPlayer.addAnimation("walk_up", runAnimation, animationPlayer.rowFrames(2, runAnimation.cols), 200, false);

		animationPlayer.addAnimation("idle_right", idleAnimation, animationPlayer.rowFrames(0, idleAnimation.cols), 200, false);
		animationPlayer.addAnimation("idle_left", idleAnimation, animationPlayer.rowFrames(0, idleAnimation.cols), 200, true);
		animationPlayer.addAnimation("idle_down", idleAnimation, animationPlayer.rowFrames(1, idleAnimation.cols), 200, false);
		animationPlayer.addAnimation("idle_up", idleAnimation, animationPlayer.rowFrames(2, idleAnimation.cols), 200, false);

		animationPlayer.addAnimation("attack_right", attackAnimation, animationPlayer.rowFrames(0, 5), 80, false);
		animationPlayer.addAnimation("attack_left", attackAnimation, animationPlayer.rowFrames(0, 5), 80, true);
		animationPlayer.addAnimation("attack_down", attackAnimation, animationPlayer.rowFrames(1, 5), 80, false);
		animationPlayer.addAnimation("attack_up", attackAnimation, animationPlayer.rowFrames(2, 5), 80, false);
	}

	public void update(double delta) {
		handleTeleporterCollision();
		updateAnimationState();
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

	void updateAnimationState() {
		xDirection = Direction.NONE;
		yDirection = Direction.NONE;

		if (input.upPressed()) {
			animationPlayer.playAndLock("walk_up");
			velocity.y -= speed;
			yDirection = Direction.NORTH;	
		} 
		else if (animationPlayer.isPlaying("walk_up")) {
			animationPlayer.unlock();
		}
		if (input.downPressed()) {
			animationPlayer.playAndLock("walk_down");
			velocity.y += speed;
			yDirection = Direction.SOUTH;
		}
		else if (animationPlayer.isPlaying("walk_down")) {
			animationPlayer.unlock();
		}
		if (input.leftPressed()) {
			animationPlayer.playAndLock("walk_left");
			velocity.x -= speed;
			xDirection = Direction.WEST;
		}
		else if (animationPlayer.isPlaying("walk_left")) {
			animationPlayer.unlock();
		}
		if (input.rightPressed()) {
			animationPlayer.playAndLock("walk_right");
			velocity.x += speed;
			xDirection = Direction.EAST;
		}
		else if (animationPlayer.isPlaying("walk_right")) {
			animationPlayer.unlock();
		}
		if (!input.moving()) {
			if (animationPlayer.isPlaying("walk_right")) {
				animationPlayer.playAnimation("idle_right", true);
			} else if (animationPlayer.isPlaying("walk_left")) {
				animationPlayer.playAnimation("idle_left", true);
			} else if (animationPlayer.isPlaying("walk_up")) {
				animationPlayer.playAnimation("idle_up", true);
			} else if (animationPlayer.isPlaying("walk_down")) {
				animationPlayer.playAnimation("idle_down", true);
			}	
		}
	}

	boolean lockAttack = false;
	public void attack() {
		animationPlayer.unlock();

		if (lockAttack)
			return;
		lockAttack = true;

		// create a hurtbox in front of the player's direction
		Box2d attackBounds = ((Box2d)(this)).clone();
		// vertical direction has prority over horizontal direction
		if (input.moving() ) {
			if (yDirection == Direction.NORTH) {
				animationPlayer.playOnceUninterrupted("attack_up");
				attackBounds.y -= height;
			} else if (yDirection == Direction.SOUTH) {
				animationPlayer.playOnceUninterrupted("attack_down");
				attackBounds.y += height;
			} else if (xDirection == Direction.WEST) {
				animationPlayer.playOnceUninterrupted("attack_left");
				attackBounds.x -= width;
			} else if (xDirection == Direction.EAST) {
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
		ArrayList<Entity> entitiesHit = gameHandle.getLevelManager().getCurrentLevel().getEntitiesInside(attackBounds);
		for (Entity e : entitiesHit) {
			e.kill();
		}

		lockAttack = false;
	}

    public PlayerInputController getPlayerInputController() {
		return input;
	}

	@Override
	public void whileDying() {

	}
}