package src.program.game;

import src.math.*;

import src.program.game.ui.KeyFrameListener;
import src.program.game.AnimationPlayer.KeyFrameEvent;

public class SlimeStateManager implements KeyFrameListener {

	Game gameHandle;
	Slime target;

	protected enum State {
		DORMANT,
		CHASING,
		ATTACKING
	}
	State currentState;
	State nextState;
	public State getCurrentState() {
		return currentState;
	}

	int aggroRadius = 256;
	int viewDistance = 128;
	int attackRadius = 48;

	public SlimeStateManager(Slime slime, Game gameHandle) {
		this.gameHandle = gameHandle;
		target = slime;
		target.getAnimationPlayer().addKeyFrameListener(this);
		currentState = State.DORMANT;
		nextState = State.DORMANT;
	}

	public void update(double delta) {
		Player player = gameHandle.getPlayer();
		AnimationPlayer ap = target.getAnimationPlayer();
		switch(currentState) {
			case DORMANT -> {
				ap.playAndLock("idle");
				if (findPlayer(viewDistance)) {
					nextState = State.CHASING;
			 		ap.unlock();
				}
			}
			case CHASING -> {
				ap.playAndLock("chasing");
	            target.velocity = target.center().normalTowards(player.center());
	            if (!findPlayer(aggroRadius)) {
	            	nextState = State.DORMANT;
	            	ap.unlock();
	            	break;
	            }
	            if (findPlayer(attackRadius)) { 
	            	nextState = State.ATTACKING;
	            	ap.unlock();
	            }
			}
			case ATTACKING -> {
				if (!findPlayer(attackRadius)) {
	            	nextState = State.CHASING;
	            	ap.unlock();
	            	break;
	            }
				ap.playOnceUninterrupted("attack");
			} 
		}
		target.getAnimationPlayer().findKeyFrameUpdates();
        currentState = nextState;
	}

	private boolean findPlayer(int range) {

		Vec2d origin = target.center();
		Vec2d dir = origin.normalTowards(gameHandle.getPlayer().center());
		Box2d player = gameHandle.getPlayer();
		Box2d[] obstacles = gameHandle.getLevelManager().getCurrentLevel().walls.toArray(Box2d[]::new);

		return RayCaster.QueryRaySim(origin, dir, player, obstacles, range, null, gameHandle.getDrawRays());
	}

	public void setGameHandle(Game gameHandle) {
		this.gameHandle = gameHandle;
	}

	final int KEYFRAME_TRIGGER_ATTACK = 3;
	@Override
	public void keyFrameReached(KeyFrameEvent keyFrame) {
		if (target.getAnimationPlayer().isPlaying("attack") && keyFrame.frame() == target.KEYFRAME_TRIGGER_ATTACK) {
			gameHandle.getPlayer().onDamaged(1);
		}
	}
}