package src.program.game.event;

import src.program.game.event.FutureEvents.FutureEvent;

/**
 * When you schedule a function in FutureEvents, you get a handle back to some info
 * about the function's execution state.
 * 
 * 
 * */
public class FutureEventHandle {
	FutureEvent event;
	/**
	 * Only constructed in FutureEvents.
	 * 
	 * 
	 * */
	protected FutureEventHandle(FutureEvent futureEvent) {
		event = futureEvent;
	}
	/**
	 * Returns whether or not this scheduled event has been executed.
	 * Note that this does not necessarily represent whether or not the event is past-due
	 * because it only updates when executeDueEvents() is called.
	 * 
	 * 
	 * */
	public boolean hasExecuted() {
		for (var lookup : FutureEvents.futureEvents) {
			if (lookup == event) {
				return false;
			}
		}
		return true;
	}
}