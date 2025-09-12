package src.program.editor;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import java.util.ArrayList;

import src.Renderer;
import src.math.*;
import src.obj.*;

import src.program.game.Slime;

public class MapRenderer extends Renderer implements MouseListener, MouseMotionListener, MouseWheelListener {

	Editor editor;
	
	protected MapStateManager mapStateManager;
	public MapStateManager getStateManager() {
		return mapStateManager;
	}
	protected LayerManagementPanel layerManagementPanel;
	
	Vec2d cameraOffset = new Vec2d();
	Vec2d dragVector = new Vec2d();
	boolean draggingMap = false;
	Vec2i selectedTilePos = Editor.INVALID_TILE.clone();
	double zoom = 1.00;

	final double MIN_ZOOM = 0.20;
	final double MAX_ZOOM = 2.00;
	final double DEFAULT_TILESIZE = 48;
	final Color GRIDCOLOR = Color.BLACK;
	final Color BACKGROUNDCOLOR = new Color(155, 173, 183, 255);

	public MapRenderer(Editor editor) {
		this.editor = editor;
		setBackground(BACKGROUNDCOLOR);
		setOpaque(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		defineRenderMethod(this::draw);

		mapStateManager = new MapStateManager(editor, this);
		layerManagementPanel = new LayerManagementPanel(editor);
	}

	// drawing
	public void draw(Graphics2D g2) {

		drawMapGrid(g2);
		if (getTileDrawSize() >= DEFAULT_TILESIZE) {
			drawTileCoords(g2);
		}
		drawMapTiles(g2);
		if (!selectedTilePos.equals(Editor.INVALID_TILE)) {
			highlightSelectedTile(g2);
		}
		if (ToolBar.drawTeleporters)
			highlightTeleporters(g2);
		highlightEnemySpawnpoints(g2);
	}

	private void drawMapGrid(Graphics2D g2) {

		BasicStroke lineWidth = new BasicStroke(1);

		g2.setColor(GRIDCOLOR);
		g2.setStroke(lineWidth);

		for (int col = -1; col <= getVisibleGridSize().x + 1; col++) {
			int drawX = (int)(col * getTileDrawSize() - (cameraOffset.x % getTileDrawSize()));
			g2.drawLine(drawX, 0, drawX, getBounds().height);
		}

		for (int row = -1; row <= getVisibleGridSize().y + 1; row++) {
			int drawY = (int)(row * getTileDrawSize() - (cameraOffset.y % getTileDrawSize()));
			g2.drawLine(0, drawY, getBounds().width, drawY);
		}
	}

	private void drawMapTiles(Graphics2D g2) {

		ArrayList<Tile>[] layerBuf = sortLayers();
		if (layerBuf == null)
			return;
		// draw each layer in ascending order
		for (int i = 0; i < layerBuf.length; i++) {
			for (Tile t : layerBuf[i]) {

				int dstX = (int)(t.mapCoords.x * getTileDrawSize() - cameraOffset.x);
				int dstY = (int)(t.mapCoords.y * getTileDrawSize() - cameraOffset.y);

				if (dstX > getBounds().width  || dstX + getTileDrawSize() < 0 ||
					dstY > getBounds().height || dstY + getTileDrawSize() < 0)
					continue;

				int srcX = t.tileSetCoords.x * editor.getActiveTileSet().tileW;
				int srcY = t.tileSetCoords.y * editor.getActiveTileSet().tileH;

				// only draw if the tile's layer is visible
				boolean visible = editor.layerIsVisible(t.layer);
				if (visible) {
					g2.drawImage (
						editor.getActiveTileSet().image,
						dstX + 1,
						dstY + 1,
						(int)Math.round(dstX + getTileDrawSize()),
						(int)Math.round(dstY + getTileDrawSize()),
						srcX,
						srcY,
						srcX + editor.getActiveTileSet().tileW,
						srcY + editor.getActiveTileSet().tileH,
						null
					);
				}
			}
		}
	}

	// compiler throws unchecked conversion for layerBuf creation, but ignore it
	@SuppressWarnings("unchecked")
	private ArrayList<Tile>[] sortLayers() {

		Map map = editor.getActiveMap();
		//System.out.println(map.numLayers);
		ArrayList<Tile>[] layerBuf = new ArrayList[map.numLayers];
		for (int i = 0; i < layerBuf.length; i++)
			layerBuf[i] = new ArrayList<>();
		
		if (map.tileData.isEmpty())
			return null;
		for (Tile t : map.tileData) {
			int order = t.layer;
			layerBuf[order].add(t);
		}
		return layerBuf;
	}

	private void drawTileCoords(Graphics2D g2) {

		g2.setColor(GRIDCOLOR);

		for (int col = -1; col <= getVisibleGridSize().x + 1; col++) {
			for (int row = -1; row <= getVisibleGridSize().y + 1; row++) {

				int mapCol = col + (int)(cameraOffset.x / getTileDrawSize());
				int mapRow = row + (int)(cameraOffset.y / getTileDrawSize());

				drawStringCentered (
					String.format("(%d, %d)", mapCol, mapRow), 
					(int)(col * getTileDrawSize() - (cameraOffset.x % getTileDrawSize()) + getTileDrawSize() / 2), 
					(int)(row * getTileDrawSize() - (cameraOffset.y % getTileDrawSize()) + getTileDrawSize() / 2), 
					g2
				);
			}
		}
	}

	private void highlightSelectedTile(Graphics2D g2) {
		if (selectedTilePos.equals(Editor.INVALID_TILE))
			return;

		var lineWidth = new BasicStroke(4);
		g2.setStroke(lineWidth);
		g2.setColor(Color.RED);
		g2.drawRect (
			(int)(getTileDrawSize() * selectedTilePos.x - cameraOffset.x),
			(int)(getTileDrawSize() * selectedTilePos.y - cameraOffset.y),
			(int)getTileDrawSize(),
			(int)getTileDrawSize()
		);
	}

	private void highlightTeleporters(Graphics2D g2) {

		g2.setColor(new Color(0.0f, 0.0f, 1.0f, 0.4f));
		for (Tile t : editor.getActiveMap().tileData) {
			if (t instanceof Teleporter tp) {

				int dstX = (int)(tp.mapCoords.x * getTileDrawSize() - cameraOffset.x);
				int dstY = (int)(tp.mapCoords.y * getTileDrawSize() - cameraOffset.y);

				g2.drawRect(dstX, dstY, (int)getTileDrawSize(), (int)getTileDrawSize());
				String str = "<" + tp.dstMapName + ">";
				drawStringCentered(str, dstX + (int)(getTileDrawSize() / 2), dstY + (int)getTileDrawSize() + 8, g2);
			}
		}
	}

	// for 
	Slime slimeInstance = new Slime();

	private void highlightEnemySpawnpoints(Graphics2D g2) {

		//g2.setColor(Color.GRAY);
		for (Tile t : editor.getActiveMap().tileData) {
			if (t instanceof EntitySpawnpoint es) {

				BufferedImage image = null;
				if (es.getEntityClass() == Slime.class)
					image = slimeInstance.getAnimationPlayer().getFrame();

				int dstX = (int)(es.mapCoords.x * getTileDrawSize() - cameraOffset.x);
				int dstY = (int)(es.mapCoords.y * getTileDrawSize() - cameraOffset.y);

				g2.drawImage(image, dstX, dstY, (int)getTileDrawSize(), (int)getTileDrawSize(), null);
				String str = "<" + es.getEntityClass().getName() + ">";
				drawStringCentered(str, dstX + (int)(getTileDrawSize() / 2), dstY + (int)getTileDrawSize() + 8, g2);
			}
		}
	}

	// input

	private boolean unhandledMapDataChange = false;

	@Override
	public void mousePressed(MouseEvent e) {

		requestFocusInWindow();

		if (SwingUtilities.isLeftMouseButton(e)) {
			switch (editor.getSelectedTool()) {
				case PENCIL ->  {
					if (tryPlaceTile(e))
						unhandledMapDataChange = true;
				}
				case ERASER -> {
					if (tryEraseTile(e))
						unhandledMapDataChange = true;
				}
				case SELECT -> {
					// weird double state containment
					editor.setSelectedMapTilePos(getMouseGridPosition(e));
            		selectedTilePos = getMouseGridPosition(e);
				} 
			}
        } 
        else if (SwingUtilities.isRightMouseButton(e)) {
        	draggingMap = true;
            dragVector.x = e.getX();
            dragVector.y = e.getY();
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (unhandledMapDataChange) {
				editor.activateMapDataListeners();
				unhandledMapDataChange = false;
			}
		}
        if (SwingUtilities.isRightMouseButton(e)) {
        	draggingMap = false;
        }
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			switch (editor.getSelectedTool()) {
				case PENCIL ->  {
					if (tryPlaceTile(e))
						unhandledMapDataChange = true;
				}
				case ERASER -> {
					if (tryEraseTile(e))
						unhandledMapDataChange = true;
				}
				case SELECT -> {
					// weird double state containment
					editor.setSelectedMapTilePos(getMouseGridPosition(e));
            		selectedTilePos = getMouseGridPosition(e);
				} 
			}
        } 
		if (draggingMap) {
			cameraOffset.x += dragVector.x - e.getX();
			cameraOffset.y += dragVector.y - e.getY();

			dragVector.x = e.getX();
			dragVector.y = e.getY();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        int notches = e.getWheelRotation();
        // +-20 percent zoom per notch
        double ZOOM_MAG = 0.20;
        zoom += notches * ZOOM_MAG; 
        if (zoom > MAX_ZOOM)
        	zoom = MAX_ZOOM;
        if (zoom < MIN_ZOOM)
        	zoom = MIN_ZOOM;
    }

    // util 

    // try to place a tile. returns whether or not the map was changed after this operation.
	private boolean tryPlaceTile(MouseEvent e) {

		Vec2i selectedTileSetTilePos = editor.getSelectedTileSetTilePos();
		if (!selectedTileSetTilePos.equals(Editor.INVALID_TILE)) {

			Vec2i mousePos 		= getMouseGridPosition(e);
			Map activeMap 		= editor.getActiveMap();
			int selectedLayer 	= editor.getSelectedLayer();
			Tile targetTile 	= activeMap.getTile(mousePos, selectedLayer);
			
			// no need to place a tile if the selected tile is the same as the target
			if (targetTile != null &&
				targetTile.tileSetCoords.x == selectedTileSetTilePos.x &&
				targetTile.tileSetCoords.y == selectedTileSetTilePos.y &&
				targetTile.layer == selectedLayer) {
					return false;
			}
			// implied else
			if (targetTile != null && 
				targetTile.layer == selectedLayer) {
					activeMap.removeTile(mousePos.x, mousePos.y, selectedLayer);
			}
			editor.getActiveMap().addTile(new Tile (
				mousePos,
				selectedTileSetTilePos,
				editor.getActiveTileSet().name,
				editor.getSelectedLayer()
			));
			return true;
		}
		return false;
	}

	// try to erase a tile. returns whether or not the map was changed after this operation.
	private boolean tryEraseTile(MouseEvent e) {

		Vec2i mousePos    = getMouseGridPosition(e);
		Map activeMap     = editor.getActiveMap();
		int selectedLayer = editor.getSelectedLayer();
		Tile targetTile   = activeMap.getTile(mousePos, selectedLayer);

		if (targetTile == null || targetTile.layer != selectedLayer)
			return false;

		activeMap.removeTile(mousePos.x, mousePos.y, selectedLayer);
		return true;
	}

	// how large a tile should be drawn to the screen
    private double getTileDrawSize() {
    	return DEFAULT_TILESIZE * zoom;
    }

    // the (col, row) position of the mouse
    private Vec2i getMouseGridPosition(MouseEvent e) {

    	double px = (e.getX() + cameraOffset.x) / getTileDrawSize();
    	double py = (e.getY() + cameraOffset.y) / getTileDrawSize();

    	// fast floor
    	int ix = (int)px;
    	int iy = (int)py;
    	return new Vec2i (
    		(px < ix)? ix - 1 : ix, 
    		(py < iy)? iy - 1 : iy
    	);
    }

    // the (cols, rows) visible size of the map
    private Vec2i getVisibleGridSize() {
    	double size = getTileDrawSize();
    	return new Vec2i((int)(getWidth() / size), (int)(getHeight() / size));
    }

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}