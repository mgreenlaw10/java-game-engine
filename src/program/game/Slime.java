package src.program.game;

import src.obj.TileSet;
import src.math.*;
import src.program.game.AnimationPlayer.AnimationFuture;

public class Slime extends Entity {

	TileSet idleAnimation;
	TileSet chasingAnimation;
	TileSet deathAnimation;
	SlimeStateManager stateManager;

	public Slime(Vec2d position, Vec2d size, Game gameHandle) {
		super(position, size, gameHandle);
		stateManager = new SlimeStateManager(this, gameHandle);
		speed = 100;
		mass = 50;

		loadResources();
		animationPlayer.playAnimation("idle", true);
	}

	public Slime() {
		this(new Vec2d(), new Vec2d(), null);
	}

	public void loadResources() {
		idleAnimation = new TileSet("res/tileset/slime_idle.png", 16, 16, 16, 0, 16, 0);
		chasingAnimation = new TileSet("res/tileset/slime_chasing.png", 16, 16, 16, 0, 16, 0);
		deathAnimation = new TileSet("res/tileset/slime_death.png", 16, 16, 16, 0, 16, 0);

		animationPlayer.addAnimation("idle", idleAnimation, animationPlayer.rowFrames(0, 4), 200, false);
		animationPlayer.addAnimation("chasing", chasingAnimation, animationPlayer.rowFrames(0, 7), 200, false);
		animationPlayer.addAnimation("death", deathAnimation, animationPlayer.rowFrames(0, 6), 100, false);
	}

	@Override
	public void setGameHandle(Game gameHandle) {
		this.gameHandle = gameHandle;
		stateManager.setGameHandle(gameHandle);
	}

	@Override
	public void update(double delta) {
		stateManager.update(delta); 
	}

	AnimationFuture deathAnimState;
	boolean firstPass = true;
	@Override
	public void whileDying() {
		if (firstPass) {
			animationPlayer.unlock();
			deathAnimState = animationPlayer.playOnceUninterrupted("death");
			firstPass = false;
		}
		if (deathAnimState != null && deathAnimState.isFinished()) { 
			lifeState = LifeState.DEAD;
		}
	}
}