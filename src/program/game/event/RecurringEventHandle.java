package src.program.game.event;

import src.program.game.event.FutureEvents.RecurringEvent;

public class RecurringEventHandle {
	RecurringEvent event;
	/**
	 * Only constructed in FutureEvents.
	 * 
	 * 
	 * */
	protected RecurringEventHandle(RecurringEvent recurringEvent) {
		event = recurringEvent;
	}
	/**
	 * Immediately end the recurring execution of this event.
	 * 
	 * 
	 * */
	public void stop() {
		/**
		 * This RecurringEventHandle will kill itself when stop() is called.
		 * */
		if (event != null) {
			FutureEvents.recurringEvents.remove(event);
			event = null;
		}
	}
}