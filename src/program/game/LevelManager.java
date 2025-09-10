package src.program.game;

import src.obj.*;
import java.io.*;
import src.math.*;
import java.awt.image.BufferedImage;
import src.program.game.Game;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;

public class LevelManager {

	Game gameHandle;

	ArrayList<Level> levels;
	Level currentLevel; 
	TileSet tileSet;

	public class Level { 

		public String name;
		public Map map;
		public ArrayList<Entity> entities;
		public ArrayList<Teleporter> teleporters;
		public ArrayList<Box2d> walls;
		public BufferedImage flatImage;

		public Level(String name, Map map) {
			this.name = name;
			this.map = map;
			entities = new ArrayList<>();
			teleporters = new ArrayList<>();
			walls = new ArrayList<>();
			buildLevel(this);
			updateFlatImage(this);
		}

		// returns a list of all entities that intersect with {bounds}
		public ArrayList<Entity> getEntitiesInside(Box2d bounds) {
			ArrayList<Entity> ret = new ArrayList<>();
			for (Entity e : entities) {
				if (e.intersects(bounds))
					ret.add(e);
			}
			return ret;
		}
	}

	public LevelManager(Game gameHandle) {
		this.gameHandle = gameHandle;
		levels = new ArrayList<>();
		tileSet = null;
	}

	@SuppressWarnings("unchecked")
	private void buildLevel(Level level) {
		// parse tiles for custom data and custom objects

		for (Tile t : level.map.tileData) {
			// tilewide data
			String customData = level.map.getCustomData(t.tileSetCoords);
			if (customData.equals("Wall")) {
				Box2d wall = new Box2d (
					t.mapCoords.x * gameHandle.getTileSize(), 
					t.mapCoords.y * gameHandle.getTileSize(), 
					gameHandle.getTileSize(), 
					gameHandle.getTileSize()
				);
				level.walls.add(wall);
			}
			if (customData.equals("Player Spawnpoint")) {
				gameHandle.setPlayerSpawnpoint (
					t.mapCoords.x * gameHandle.getTileSize(),
					t.mapCoords.y * gameHandle.getTileSize()
				);
			}
			// custom object
			if (t instanceof Teleporter tp) {
				level.teleporters.add(tp);
				tp.setGameHandle(gameHandle);
			}
			if (t instanceof EntitySpawnpoint es) {
				try {
					Entity e = (Entity)es.getEntityClass().getDeclaredConstructor().newInstance();
					int x = t.mapCoords.x * gameHandle.getTileSize();
					int y = t.mapCoords.y * gameHandle.getTileSize();
					int w = gameHandle.getTileSize();
					int h = gameHandle.getTileSize();
					Vec2d pos = new Vec2d(x, y);
					Vec2d size = new Vec2d(w, h);
					e.setPosition(pos);
					e.setSize(size);
					e.setGameHandle(gameHandle);
					level.entities.add(e);
				}
				catch (
					NoSuchMethodException |
					InstantiationException | 
					IllegalAccessException | 
					InvocationTargetException e
				) {
					System.out.println("Spawning enemies from active map failed");
				}
			}
		}
	}

	public void loadLevelFromFile(String levelName, String fPath) {
		try {
			var file = new FileInputStream(fPath);
			var mapReader = new ObjectInputStream(file);
			
			Map map = (Map)mapReader.readObject();
			Level l = new Level(levelName, map);
			levels.add(l);

			mapReader.close();
			file.close();

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("LOAD MAP FROM " + fPath + " FAILED");
			e.printStackTrace();
		}
	}

	public ArrayList<Level> getLevels() {
		return levels;
	}
	public Level getCurrentLevel() {
		return currentLevel;
	}
	public void setCurrentLevel(String name) {
		for (Level l : levels) {
			if (l.name.equals(name))
				setCurrentLevel(l);
		}
	}
	public void setCurrentLevel(Level level) {
		// transfer player to new map
		Player player = gameHandle.getPlayer();
		if (currentLevel != null && 
			currentLevel.entities.contains(player)) {
				currentLevel.entities.remove(player);
		}
		currentLevel = level;
		currentLevel.entities.add(player);
	}

	public TileSet getTileSet() {
		return tileSet;
	}
	public void loadTileSet(TileSet ts) {
		tileSet = ts;
	}

	public static Vec2i getMapSize(Map map) {

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Tile tile : map.tileData) {
				if (tile.mapCoords.x < minX)
					minX = tile.mapCoords.x;
				if (tile.mapCoords.x > maxX)
					maxX = tile.mapCoords.x;
				if (tile.mapCoords.y < minY)
					minY = tile.mapCoords.y;
				if (tile.mapCoords.y > maxY)
					maxY = tile.mapCoords.y;
		}
		return new Vec2i(maxX - minX + 1,
						 maxY - minY + 1);
	}

	public void updateFlatImage(Level level) {

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Tile tile : level.map.tileData) {
				if (tile.mapCoords.x < minX)
					minX = tile.mapCoords.x;
				if (tile.mapCoords.x > maxX)
					maxX = tile.mapCoords.x;
				if (tile.mapCoords.y < minY)
					minY = tile.mapCoords.y;
				if (tile.mapCoords.y > maxY)
					maxY = tile.mapCoords.y;
		}
		int imgW = (maxX - minX + 1) * tileSet.tileW;
		int imgH = (maxY - minY + 1) * tileSet.tileH;

		BufferedImage mapImg = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D canvas = (Graphics2D)mapImg.createGraphics();
		for (Tile tile : level.map.tileData) {
			canvas.drawImage (
				tileSet.image.getSubimage (	
					tile.tileSetCoords.x * tileSet.tileW, 
					tile.tileSetCoords.y * tileSet.tileH,
					tileSet.tileW,
					tileSet.tileH 
				),
				(tile.mapCoords.x - minX) * tileSet.tileW,
				(tile.mapCoords.y - minY) * tileSet.tileH,
				tileSet.tileW,
				tileSet.tileH,
				null
			);
		}
		level.flatImage = mapImg;
	}
}