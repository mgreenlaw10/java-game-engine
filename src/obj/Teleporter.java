package src.obj;

import src.program.game.Game;
import src.math.*;
public class Teleporter extends Tile {

	Game gameHandle;
	// if {dstMapName} is null, this teleporter will point to the same map
	public String dstMapName;
	public Vec2d dstCoords;

	public Teleporter(int col, int row, int tsCol, int tsRow,
	 	String dstMapName, Vec2d dstCoords, TileSet ts, Game gameHandle) {

		super(col, row, tsCol, tsRow, ts);
		this.gameHandle = gameHandle;
		this.dstMapName = dstMapName;
		this.dstCoords = dstCoords;
	}

	public Teleporter(Vec2i mapCoords, Vec2i tileSetCoords, String tileSetName, int layer,
					  String dstMapName, Vec2d dstCoords, Game gameHandle) {
		
		super(mapCoords, tileSetCoords, tileSetName, layer);
		this.gameHandle = gameHandle;
		this.dstMapName = dstMapName;
		this.dstCoords = dstCoords;
	}

	// must be called when the map loads because {gameHandle} is set to null by the editor
	public void setGameHandle(Game gameHandle) {
		this.gameHandle = gameHandle;
	}

	public void onPlayerCollision() {
		if (dstMapName != null) {
			gameHandle.getLevelManager().setCurrentLevel(dstMapName);
		}
		Vec2d scaledDstPos = new Vec2d (
			gameHandle.getTileSize() * dstCoords.x,
			gameHandle.getTileSize() * dstCoords.y
		);
		gameHandle.getPlayer().setPosition(scaledDstPos);
	}
}