package src.program.game.event;

import java.util.ArrayList;

public class FutureEvents {
	/**
	 * Purely static class. No construction.
	 * 
	 * 
	 * */
	private FutureEvents() {}
	/**
	 * A FutureEvent encapsulates an event with its delay and the time that it was scheduled.
	 * 
	 * 
	 * */
	protected static class FutureEvent {
		GameEvent event;
		long delay;
		long startTime;
		protected FutureEvent(GameEvent pEvent, long pDelay, long pStartTime) {
			event = pEvent;
			delay = pDelay;
			startTime = pStartTime;
		}
	}
	/**
	 * A RecurringEvent is like a FutureEvent that doesn't automatically expire,
	 * and can be activated and deactivated. It also contains an event handle because
	 * the first execution can be uniquely timed and out of sync with the rest.
	 * 
	 * 
	 * */
	protected static class RecurringEvent {
		GameEvent event;
		FutureEventHandle initialExecution;
		long delay;
		long lastExecutionTime;
		boolean active;
		protected RecurringEvent(GameEvent pEvent, FutureEventHandle pInitialExecution, long pDelay, long pLastExecutionTime, boolean pActive) {
			event = pEvent;
			initialExecution = pInitialExecution;
			delay = pDelay;
			lastExecutionTime = pLastExecutionTime;
			active = pActive;
		}
		protected void setActive(boolean val) {
			active = val;
		}
		private void setLastExecutionTime(long time) {
			lastExecutionTime = time;
		}
		/**
		 * Only called as soon as initialExecution expires.
		 * */
		private void nullifyInitialExecution() {
			initialExecution = null;
		}
	}
	/**
	 * Keep static lists of all unhandled events.
	 * The lists are refreshed whenever executeDueEvents() is called.
	 * 
	 * */
	protected static ArrayList<FutureEvent> futureEvents = new ArrayList<>();
	protected static ArrayList<FutureEvent> lockedEvents = new ArrayList<>();
	protected static ArrayList<RecurringEvent> recurringEvents = new ArrayList<>();	
	/**
	 * Schedule an event to be invoked some time later.
	 * 
	 * @param gameEvent: Event to be executed.
	 * @param delay: How many milliseconds later the event should execute.
	 * 
	 * 
	 * */
	public static FutureEventHandle doLater(GameEvent gameEvent, long delay) {
		var futureEvent = new FutureEvent(gameEvent, delay, System.currentTimeMillis());
		futureEvents.add(futureEvent);
		return new FutureEventHandle(futureEvent);
	}
	/**
	 * You can also lock an event from being executable for a certain amount of time.
	 * This only works if you keep the LockedEventHandle and check its status.
	 * 
	 * @param gameEvent: Event to be locked.
	 * @param delay: How many milliseconds later the event should unlock.
	 * 
	 * 
	 * */
	 public static LockedEventHandle lockEvent(GameEvent gameEvent, long delay) {
	 	var futureEvent = new FutureEvent(gameEvent, delay, System.currentTimeMillis());
	 	lockedEvents.add(futureEvent);
	 	return new LockedEventHandle(futureEvent);
	 }
	/**
	 * You can also register an event to be periodically executed.
	 * You must keep the RecurringEventHandle and manually cancel the execution.
	 * 
	 * 
	 **/
	public static RecurringEventHandle startRecurringEvent(GameEvent gameEvent, long initialDelay, long delay) {
		/**
		 * Schedule inital execution.
		 * */
		var recurringEvent = new RecurringEvent(gameEvent, doLater(gameEvent, initialDelay), initialDelay, delay, false);
		recurringEvents.add(recurringEvent);
		return new RecurringEventHandle(recurringEvent);
	}
	/**
	 * Execute and remove all past-due future events. 
	 * Remove all past-due locked events.
	 * 
	 * This should be called at least once every update loop.
	 * 
	 * 
	 * */
	public static void executeDueEvents() {
		ArrayList<FutureEvent> updatedFutureEventList = new ArrayList<>();
		ArrayList<FutureEvent> updatedLockedEventList = new ArrayList<>();
		long now = System.currentTimeMillis(); // Current time to ms.
		for (FutureEvent event : futureEvents) {
			/**
			 * Execute the event if it has passed its delay,
			 * or keep it in memory for later.
			 * */
			if (now - event.startTime >= event.delay) {
				event.event.execute();
			} else {
				updatedFutureEventList.add(event);
			}
		}
		for (FutureEvent event : lockedEvents) {
			/**
			 * Keep in memory if the event is not due.
			 * */
			if (!(now - event.startTime >= event.delay)) {
				updatedLockedEventList.add(event);
			}
		}
		futureEvents = updatedFutureEventList;
		lockedEvents = updatedLockedEventList;
		executeRecurringEvents();
	}
	/**
	 * Execute all past-due recurring events, but first
	 * check if their initial execution has triggrered.
	 * 
	 * 
	 * */
	static void executeRecurringEvents() {
		long now = System.currentTimeMillis(); // Current time to ms.
		for (RecurringEvent event : recurringEvents) {
			if (event.initialExecution == null || event.initialExecution.hasExecuted()) {
				event.nullifyInitialExecution();
				event.setActive(true);
			}
			if (event.active && now - event.lastExecutionTime >= event.delay) {
				event.event.execute();
				event.setLastExecutionTime(now);
			}
		}
	}
}