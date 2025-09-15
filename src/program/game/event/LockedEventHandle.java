package src.program.game.event;

import src.program.game.event.FutureEvents.FutureEvent;

/**
 * When you lock a function in FutureEvents, you get a handle back to some info
 * about the function's state.
 * 
 * 
 * */
public class LockedEventHandle {
	FutureEvent event;
	/**
	 * Only constructed in FutureEvents.
	 * 
	 * 
	 * */
	protected LockedEventHandle(FutureEvent futureEvent) {
		event = futureEvent;
	}
	/**
	 * Returns whether or not this locked event is still locked.
	 * Note that this does not necessarily represent whether or not the event is past-due
	 * because it only updates when executeDueEvents() is called.
	 * 
	 * 
	 * */
	public boolean isLocked() {
		for (var lookup : FutureEvents.lockedEvents) {
			if (lookup == event) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Manually unlock this locked event.
	 * Returns whether or not the event was already unlocked when this was called.
	 * 
	 * 
	 * */
	public boolean unlockImmediately() {
		boolean match = false;
		for (var lookup : FutureEvents.lockedEvents) {
			if (lookup == event) {
				match = true;
			}
		}
		if (match) {
			FutureEvents.lockedEvents.remove(event);
			return true;
		}
		return false;
	}
}