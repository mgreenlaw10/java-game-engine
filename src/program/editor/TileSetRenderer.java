package src.program.editor;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;

import src.obj.Map;
import src.obj.Texture;
import src.obj.TileSet;

import src.Renderer;
import src.math.*;

public class TileSetRenderer extends Renderer implements MouseListener, MouseMotionListener {

	Editor editor;
	
	Vec2i hoveredTileCoords;
	Vec2i selectedTileCoords;

	public TileSetRenderer(Editor editor) {

		this.editor = editor;
		this.hoveredTileCoords = Editor.INVALID_TILE.clone();
		this.selectedTileCoords = Editor.INVALID_TILE.clone();
		
		addMouseListener(this);
		addMouseMotionListener(this);

		defineRenderMethod(this::draw);
	}

	public void draw(Graphics2D g2) {
		drawTileSet(g2);
		if (tileSelected()) {
			highlightSelectedTile(g2);		
		}
	}

	private void drawTileSet(Graphics2D g2) {

		TileSet ts = editor.getActiveTileSet();
		int MARGIN = 1;

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(MARGIN));

		for (int row = 0; row < ts.rows; row++) { 
			for (int col = 0; col < ts.cols; col++) {
				g2.drawImage (
					ts.getFrame(col, row), 
					(int)Math.round(getTileDrawSize() * col) + MARGIN,
					(int)Math.round(getTileDrawSize() * row) + MARGIN,
					(int)Math.round(getTileDrawSize()) - MARGIN,
					(int)Math.round(getTileDrawSize()) - MARGIN,
					null
				);
				int vLineX1 = (int)Math.round(getTileDrawSize() * (col + 1));
				int vLineY1 = 0;		
				int vLineX2 = (int)Math.round(getTileDrawSize() * (col + 1));
				int vLineY2 = getHeight();		
				g2.drawLine(vLineX1, vLineY1, vLineX2, vLineY2);
			}
			int hLineX1 = 0;
			int hLineY1 = (int)Math.round(getTileDrawSize() * (row + 1));		
			int hLineX2 = getWidth();
			int hLineY2 = (int)Math.round(getTileDrawSize() * (row + 1));
			g2.drawLine(hLineX1, hLineY1, hLineX2, hLineY2);
		}
	}

	private void highlightSelectedTile(Graphics2D g2) {

		g2.setColor(Color.RED);
		var lineWidth = new BasicStroke(4);
		g2.setStroke(lineWidth);
		g2.drawRect (
			(int)Math.round(selectedTileCoords.x * getTileDrawSize()),
		 	(int)Math.round(selectedTileCoords.y * getTileDrawSize()),
		 	(int)getTileDrawSize(),
		 	(int)getTileDrawSize()
		 );
	}

	private boolean tileHovered() {
		return (!hoveredTileCoords.equals(Editor.INVALID_TILE));
	}

	private boolean tileSelected() {
		return (!selectedTileCoords.equals(Editor.INVALID_TILE));
	}

	// fit the whole image in the space
	private double getTileDrawSize()  {
    	double tileW = (double)getBounds().width / editor.getActiveTileSet().cols;
    	double tileH = (double)getBounds().height / editor.getActiveTileSet().rows;
    	return Math.min(tileW, tileH);
	}

	// mouse input

	public void mouseExited(MouseEvent e) {
		hoveredTileCoords = Editor.INVALID_TILE.clone();
	}

	public void mouseMoved(MouseEvent e) {
		updateHoveredTile(e);
	}

	public void mouseClicked(MouseEvent e) {
		updateSelectedTile(e);
	}

	public void mousePressed(MouseEvent e) {
		requestFocusInWindow();
		updateSelectedTile(e);
	}

	private void updateHoveredTile(MouseEvent e) {
		hoveredTileCoords = getMouseGridPosition(e);
	}

	private void updateSelectedTile(MouseEvent e) { 

		selectedTileCoords = getMouseGridPosition(e);
		if (coordsOutOfBounds(selectedTileCoords)) {
			editor.setSelectedTileSetTilePos(Editor.INVALID_TILE);
		}
		else {
			editor.setSelectedTileSetTilePos(selectedTileCoords);
		}
	}

	private boolean coordsOutOfBounds(Vec2i coords) {
		return coords.x < 0 || coords.x >= editor.getActiveTileSet().cols ||
			   coords.y < 0 || coords.y >= editor.getActiveTileSet().rows;
	}

	private Vec2i getMouseGridPosition(MouseEvent e) {
		double px = e.getX() / getTileDrawSize();
    	double py = e.getY() / getTileDrawSize();

    	// fast floor
    	int ix = (int)px;
    	int iy = (int)py;
    	return new Vec2i (
    		(px < ix)? ix - 1 : ix, 
    		(py < iy)? iy - 1 : iy
    	);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {}
}