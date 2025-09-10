package src.obj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import src.math.Vec2i;

public class Map implements java.io.Serializable {

	public String name = "";

	// list of all tiles
	public ArrayList<Tile> tileData;

	public int minCol = Integer.MIN_VALUE;
	public int minRow = Integer.MIN_VALUE;
	public int numLayers = 1;

	// map relating tiles on the spritesheet to custom data groups
	public HashMap<String, ArrayList<Vec2i>> customDataMap;

	public static final String[] CUSTOM_ATTRIBUTES = {
		"None",
		"Wall",
		"Player Spawnpoint"
	};

	public Map() {
		tileData = new ArrayList<>();
		customDataMap = new HashMap<>();

		for (int i = 1; i < CUSTOM_ATTRIBUTES.length; i++) {
			customDataMap.put(CUSTOM_ATTRIBUTES[i], new ArrayList<>());
		};
	}

	public Map(String name) {
		this.name = name;
		tileData = new ArrayList<>();
		customDataMap = new HashMap<>();

		for (int i = 1; i < CUSTOM_ATTRIBUTES.length; i++) {
			customDataMap.put(CUSTOM_ATTRIBUTES[i], new ArrayList<>());
		};
	}

	public String getCustomData(Vec2i tsCoords) {
		
		for (int i = 1; i < CUSTOM_ATTRIBUTES.length; i++) {
			for (Vec2i tileCoords : customDataMap.get(CUSTOM_ATTRIBUTES[i]))
				if (tileCoords.equals(tsCoords))
					return CUSTOM_ATTRIBUTES[i];
		}	

		return CUSTOM_ATTRIBUTES[0];
	}

	public void updateCustomData(Vec2i tsCoords, String newData) {

		for (int i = 1; i < CUSTOM_ATTRIBUTES.length; i++) {

			// can't just remove from an arraylist while iterating through it
			Vec2i match = null;
			for (Vec2i tileCoords : customDataMap.get(CUSTOM_ATTRIBUTES[i]))
				if (tileCoords.equals(tsCoords))
					match = tileCoords;

			if (match != null) 
			 	customDataMap.get(CUSTOM_ATTRIBUTES[i]).remove(match);
			
			if (CUSTOM_ATTRIBUTES[i].equals(newData)) 
				customDataMap.get(CUSTOM_ATTRIBUTES[i]).add(tsCoords);
			
		}
	}

	public Tile getTile(int col, int row) {

		for (Tile t : tileData) {
			if (t.mapCoords.x == col &&
				t.mapCoords.y == row) {
				return t;
			}
		}
		return null;
	}

	public Tile getTile(Vec2i pos) { 

		for (Tile t : tileData) {
			if (t.mapCoords.x == pos.x &&
				t.mapCoords.y == pos.y) {
				return t;
			}
		}
		return null;
	}

	public Tile getTile(Vec2i pos, int layer) {

		for (Tile t : tileData)
			if (t.mapCoords.x == pos.x &&
				t.mapCoords.y == pos.y &&
				t.layer == layer) 
					return t;
		return null;
	}

	public int getMinCol() {
		int result = Integer.MAX_VALUE;
		for (Tile t : tileData) {
			if (t.mapCoords.x < result)
				result = t.mapCoords.x;
		}
		return result;
	}

	public int getMinRow() {
		int result = Integer.MAX_VALUE;
		for (Tile t : tileData) {
			if (t.mapCoords.y < result)
				result = t.mapCoords.y;
		}
		return result;
	}

	public void addTile(Tile tile) {
		tileData.add(tile);
	}

	public void removeTile(int col, int row, int layer) {
		Tile target;	
		if ((target = getTile(new Vec2i(col, row), layer)) != null) {
			Iterator<Tile> it = tileData.iterator();
			while (it.hasNext()) {
				Tile tile = it.next();
				if (tile.equals(target)) {
					it.remove();
				}
			}
		}
	}

	// calculates the dimensional size and minimum col/row of this map
	// then writes them to their respective parameters.
	public void querySize(Vec2i dimension, Vec2i minCoords) {

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (Tile tile : tileData) {
				if (tile.mapCoords.x < minX)
					minX = tile.mapCoords.x;
				if (tile.mapCoords.x > maxX)
					maxX = tile.mapCoords.x;
				if (tile.mapCoords.y < minY)
					minY = tile.mapCoords.y;
				if (tile.mapCoords.y > maxY)
					maxY = tile.mapCoords.y;
		}
		if (dimension != null) {
			dimension.x = maxX - minX + 1;
			dimension.y = maxY - minY + 1;	
		}
		if (minCoords != null) {
			minCoords.x = minX;
			minCoords.y = minY;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map clone() {
		Map ret = new Map();
		ret.tileData = (ArrayList<Tile>)this.tileData.clone();
		ret.name = this.name;
		ret.numLayers = this.numLayers;
		ret.minCol = this.minCol;
		ret.minRow = this.minRow;
		ret.customDataMap = (HashMap<String, ArrayList<Vec2i>>)this.customDataMap.clone();
		return ret;
	}
}