package src.program.game.event;

@FunctionalInterface
public interface GameEvent {
	/**
	 * Lambda GameEvents are passed to FutureEvents to be executed later.
	 * 
	 * */
	void execute();
}