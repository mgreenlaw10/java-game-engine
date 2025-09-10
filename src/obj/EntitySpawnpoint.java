package src.obj;

import src.program.game.Entity;
import src.math.Vec2i;
import src.program.game.Game;

public class EntitySpawnpoint extends Tile {

	Game gameHandle;
	Class entityClass;

	public EntitySpawnpoint(Vec2i mapCoords, Vec2i tileSetCoords, String tileSetName, int layer, Class entityClass, Game gameHandle) {
		super(mapCoords, tileSetCoords, tileSetName, layer);
		this.gameHandle = gameHandle;
		this.entityClass = entityClass;
	}

	// must be called when the map loads because {gameHandle} is set to null by the editor
	public void setGameHandle(Game gameHandle) {
		this.gameHandle = gameHandle;
	}

	public Class getEntityClass() {
		return entityClass;
	}
}