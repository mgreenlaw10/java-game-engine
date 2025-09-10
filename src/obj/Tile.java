package src.obj;

import src.math.Vec2i;

public class Tile implements java.io.Serializable {

	public Vec2i mapCoords;
	public Vec2i tileSetCoords;
	public String tileSetName;
	public int layer;
	
	public Tile(int col, int row, int tsCol, int tsRow, TileSet ts) {
		this.mapCoords = new Vec2i(col, row);
		this.tileSetCoords = new Vec2i(tsCol, tsRow);
		this.tileSetName = ts.name;
		this.layer = 1;
	}

	public Tile(Vec2i mapCoords, Vec2i tileSetCoords, String tileSetName, int layer) {
		this.mapCoords = mapCoords;
		this.tileSetCoords = tileSetCoords;
		this.tileSetName = tileSetName;
		this.layer = layer;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Tile t) {
			return  t.mapCoords.x == mapCoords.x &&
					t.mapCoords.y == mapCoords.y &&
					t.tileSetCoords.x == tileSetCoords.x &&
					t.tileSetCoords.y == tileSetCoords.y;
		}
		return false;
	}

}