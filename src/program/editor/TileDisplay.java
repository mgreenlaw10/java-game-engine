package src.program.editor;

import src.math.*;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import src.obj.*;

public class TileDisplay extends src.Renderer {

	Editor editor;

	Vec2i targetCoords;
	TileSet tileSet;

	public TileDisplay(TileSet tileSet, Editor editor) {
		super();
		this.editor = editor;
		this.tileSet = tileSet;
		targetCoords = Editor.INVALID_TILE.clone();
		defineRenderMethod(this::draw);
	}

	public void setTarget(Vec2i targetCoords) {
		this.targetCoords = targetCoords;
	}

	public void deleteTarget() {
		targetCoords = Editor.INVALID_TILE.clone();
	}

	public void draw(Graphics2D g2) {

		if (targetCoords.equals(Editor.INVALID_TILE)) {
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setColor(Color.BLACK);
			drawStringCentered("NONE", getWidth() / 2, getHeight() / 2, g2);
			return;
		}

		BufferedImage displayImg = tileSet.getFrame(targetCoords.x, targetCoords.y);
		g2.drawImage(displayImg, 0, 0, getWidth(), getHeight(), null);
	}
}