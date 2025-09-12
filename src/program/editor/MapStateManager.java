package src.program.editor;

import java.util.ArrayList;
import src.program.editor.ui.MapDataListener;
import src.obj.Map;

/**
 * When the map is edited, push state to the cache
 * When ctrl + z pressed, move state pointer back
 * When ctrl + y pressed, move state pointer forward
 * One MapStateManager should be owned by each MapRenderer
*/

public class MapStateManager implements MapDataListener {

	Editor editor;
	MapRenderer renderer;

	protected StateCache stateCache;
	public StateCache getStateCache() {
		return stateCache;
	}
	final int CACHESIZE = 16;

	// acts like stack
	public class StateCache {
		public Map[] cache;
		public int top;
		public int cur;

		public StateCache() {
			cache = new Map[CACHESIZE];
			top = -1;
			cur = -1;
		}

		public Map getCurrentState() {
			return cache[cur].clone();
		}

		public int getNumStates() {
			for (int i = 1; i < CACHESIZE; i++) {
				if (cache[i] == null)
					return i;
			}
			return CACHESIZE;
		}

		public Map popBottomAndShift() {
			Map ret = cache[1];
			for (int i = 2; i < CACHESIZE; i++) { 
				cache[i - 1] = cache[i];
			}
			cache[CACHESIZE - 1] = null;
			if (ret != null) {
				top = Math.max(top - 1, 0);
				if (cur > 0)
					cur--;
			}
			return ret;
		}
	}

	public MapStateManager(Editor editorHandle, MapRenderer rendererHandle) {
		editor = editorHandle;
		renderer = rendererHandle;
		editor.addMapDataListener(this);
		stateCache = new StateCache();
		pushState(editor.getActiveMap());
	}

	@Override
	public void mapEdited() {
		// every time the map changes, save the state if the active map is this one
		Map map = editor.getActiveMap();
		if (editor.centerPanel.getMapRenderer() == renderer) {
			pushState(map);
		}
	}

	void pushState(Map map) {
		// if we edit the map from a previous state, destroy all
		// concurrent states that branch from the previous state
		stateCache.top = stateCache.cur + 1;
		if (stateCache.top == CACHESIZE) {
			stateCache.popBottomAndShift();
		}
		stateCache.cache[stateCache.top++] = map.clone();
		stateCache.cur = stateCache.top - 1;
	}

	Map popState() {
		if (stateCache.top == 0)
			return null;
		Map ret = stateCache.cache[--stateCache.top];
		stateCache.cache[stateCache.top] = null;

		if (stateCache.cur >= stateCache.top) {
			stateCache.cur = (stateCache.top == 0)? 0 : stateCache.top - 1;
		}
		return ret;
	}

	public Map getCurrentMapState() {
		return stateCache.cache[stateCache.cur].clone();
	}

	// KEYBOARD INPUT
	public void undo() {
		stateCache.cur--;
		if (stateCache.cur < 0)
			stateCache.cur = 0;
		editor.setActiveMap(stateCache.getCurrentState());
	}

	public void redo() {
		stateCache.cur++;
		if (stateCache.cur >= CACHESIZE || stateCache.cur >= stateCache.top)
			stateCache.cur--;
		editor.setActiveMap(stateCache.getCurrentState());
	}
}