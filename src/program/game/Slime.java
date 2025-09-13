package src.program.game;

import src.obj.TileSet;
import src.math.*;
import src.program.game.AnimationPlayer.AnimationFuture;
import src.program.game.SlimeStateManager.State;

public class Slime extends Entity {

	TileSet idleAnimation;
	TileSet chasingAnimation;
	TileSet deathAnimation;
	TileSet attackAnimation;
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

	public final int KEYFRAME_TRIGGER_ATTACK = 3;

	public void loadResources() {
		idleAnimation = new TileSet("res/tileset/slime_idle.png", 16, 16, 16, 0, 16, 0);
		chasingAnimation = new TileSet("res/tileset/slime_chasing.png", 16, 16, 16, 0, 16, 0);
		deathAnimation = new TileSet("res/tileset/slime_death.png", 16, 16, 16, 0, 16, 0);
		attackAnimation = new TileSet("res/tileset/slime_attacking.png", 16, 16, 16, 0, 16, 0);

		int[] ATTACK_KEYFRAMES = new int[] {KEYFRAME_TRIGGER_ATTACK};
		
		animationPlayer.addAnimation("idle", idleAnimation, animationPlayer.rowFrames(0, 4), 200, null, false);
		animationPlayer.addAnimation("chasing", chasingAnimation, animationPlayer.rowFrames(0, 7), 200, null, false);
		animationPlayer.addAnimation("death", deathAnimation, animationPlayer.rowFrames(0, 6), 100, null, false);
		animationPlayer.addAnimation("attack", attackAnimation, animationPlayer.rowFrames(0, 6), 100, ATTACK_KEYFRAMES, false);
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

	@Override
	public String getStateString() {
		State state = stateManager.getCurrentState();
		if (lifeState == LifeState.DYING) {
			return "DYING";
		}
		if (state == State.DORMANT) {
			return "DORMANT";
		}
		if (state == State.CHASING) {
			return "CHASING";
		}
		if (state == State.ATTACKING) {
			return "ATTACKING";
		}
		return "?";
	}
}