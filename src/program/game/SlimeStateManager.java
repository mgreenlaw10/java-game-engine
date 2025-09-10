package src.program.game;

public class SlimeStateManager {

	Game gameHandle;
	Slime target;

	protected enum State {
		DORMANT,
		CHASING
	}
	State currentState;
	State nextState;

	double aggroRadius = 128;
	double viewDistance = 256;
	double stopRadius = 16;

	public SlimeStateManager(Slime slime, Game gameHandle) {
		this.gameHandle = gameHandle;
		target = slime;
		currentState = State.DORMANT;
		nextState = State.DORMANT;
	}

	public void update(double delta) {
		Player player = gameHandle.getPlayer();
		AnimationPlayer ap = target.getAnimationPlayer();
		switch(currentState) {
		case DORMANT -> {
			ap.playAndLock("idle");
			if (target.center().distanceFrom(player.center()) < aggroRadius) {
					nextState = State.CHASING;
					ap.unlock();
			}
		}
		case CHASING -> {
			ap.playAndLock("chasing");
            target.velocity = target.center().normalTowards(player.center());
            if (target.center().distanceFrom(player.center()) > viewDistance) {
            	nextState = State.DORMANT;
            	ap.unlock();
            }
		}
		}
        currentState = nextState;
	}

	public void setGameHandle(Game gameHandle) {
		this.gameHandle = gameHandle;
	}
}