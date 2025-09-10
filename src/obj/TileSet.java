package src.obj;

import java.awt.image.BufferedImage;

import src.program.editor.Editor;
import java.awt.Rectangle;

public class TileSet implements java.io.Serializable {

	public String name = "NONE";

	public BufferedImage image;
	public int cols, rows;
	public int tileW, tileH;
	public int xOffset, yOffset;
	public int xStep, yStep;

	public TileSet(String filePath, int tileSize) {
		this("", filePath, tileSize);
	}

	public TileSet(String name, String filePath, int tileSize) {
		this(name, filePath, tileSize, tileSize, 0, 0, 0, 0);
	}

	public TileSet(String filePath, int tileW, int tileH, int xOffset, int yOffset, int xStep, int yStep) {
		this("", filePath, tileW, tileH, xOffset, yOffset, xStep, yStep);
	}

	public TileSet(String name, String filePath, int tileW, int tileH, int xOffset, int yOffset, int xStep, int yStep) {
		this.name = name;
		this.image = (new Texture(filePath)).image;
		this.tileW = tileW;
		this.tileH = tileH;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xStep = xStep;
		this.yStep = yStep;
		this.cols = (image.getWidth()  - 2 * xOffset + tileW) / (tileW + xStep) - 1;
		this.rows = (image.getHeight() - 2 * yOffset + tileH) / (tileH + yStep) - 1;
	}
        
    public BufferedImage getFrame(int col, int row) {
        return image.getSubimage ( 
            xOffset + (tileW + xStep) * col, 
            yOffset + (tileH + yStep) * row, 
            tileW,
            tileH 
        );
    }

	public Rectangle getSrcRect(int col, int row) {
		return new Rectangle (
			xOffset + (tileW + xStep) * col, 
			yOffset + (tileH + yStep) * row, 
			tileW,
			tileH
		);
	}
}