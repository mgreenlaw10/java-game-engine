package src.program.editor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.io.*;
import src.program.editor.*;
import src.program.editor.ui.SelectedTileListener;
import src.program.editor.ui.TileSetListener;
import src.obj.*;

import java.util.HashMap;
import src.math.Vec2i;
import src.gui.*;
import java.awt.image.BufferedImage;

public class LeftPanel extends Panel implements TileSetListener {

	Editor editor;

	protected TabbedPanel tabPanel;
	protected ListPanel tileSetDetailsPanel;
	protected CustomTileDataPanel customTileDataPanel;
	protected Label label_tileSetDetails;
	protected Label label_tileSetName;
	protected Label label_tileSetSize;
	protected Label label_tileSize;

	public enum ToolMode {
		PAINT,
		ERASE
	};

	private ToolMode currentToolMode; 

	public ToolMode getTool() {
		return currentToolMode;
	}
	
	NinePatchTexture background = Editor.PANEL_BG_TEXTURE;
	
	public LeftPanel(Editor editor) {

		super();
		this.editor = editor;
		this.editor.addTileSetListener(this);
		this.editor.addSelectedTileListener(this);
		this.currentToolMode = ToolMode.PAINT;
		
		setPreferredBounds(16, 32, 352, 688);
		background.setSize(352, 688);
		setBG(background);

		tabPanel = new TabbedPanel();
		createNewTab(editor.getActiveTileSet(), true);
		customTileDataPanel = new CustomTileDataPanel(editor);
		
		addComponent(customTileDataPanel, 16, 352);
		addComponent(tabPanel, 16, 16, 318, 318);
		buildTileSetDetailsPanel();
	}

	public void createNewTab(TileSet map, boolean select) {
		// if the map is null or unnamed
		TileSetRenderer tab = new TileSetRenderer(editor);
		String tabName = (editor.getActiveTileSet() == null || editor.getActiveTileSet().name.equals(""))? "*unnamed*" : editor.getActiveTileSet().name;
		tabPanel.addTab(tabName, tab);
		if (select) {
			tabPanel.setSelectedComponent(tab);
		}
	}

	private void buildTileSetDetailsPanel() {
		tileSetDetailsPanel = new ListPanel(24);
		tileSetDetailsPanel.setPreferredSize(144, 144);

		label_tileSetDetails = new Label("TILE SET DETAILS", Label.LEFT);
		label_tileSetName = new Label("Name: NONE", Label.LEFT);
		label_tileSetSize = new Label("Size: EMPTY", Label.LEFT);
		label_tileSize = new Label("Tile Size: 0", Label.LEFT);

		Vec2i textOffset = new Vec2i(4, 0);
		label_tileSetDetails.offset = textOffset;
		label_tileSetName.offset = textOffset;
		label_tileSetSize.offset = textOffset;
		label_tileSize.offset = textOffset;

		tileSetDetailsPanel.addCell(label_tileSetDetails, 0);
		tileSetDetailsPanel.addCell(label_tileSetName, 1);
		tileSetDetailsPanel.addCell(label_tileSetSize, 2);
		tileSetDetailsPanel.addCell(label_tileSize, 3);
		addComponent(tileSetDetailsPanel, 16, 448);
	}

	@Override
	public void tileSetChanged() {
		updateTileSetNameLabel();
		updateTileSetSizeLabel();
		updateTileSizeLabel();
	}

	private void updateTileSetNameLabel() {
		TileSet tileSet = editor.getActiveTileSet();
		label_tileSetName.setText("Name: " + tileSet.name);
	}

	private void updateTileSetSizeLabel() {
		TileSet tileSet = editor.getActiveTileSet();
		label_tileSetSize.setText("Size: " + (new Vec2i(tileSet.cols, tileSet.rows)).toString());
	}

	private void updateTileSizeLabel() {
		TileSet tileSet = editor.getActiveTileSet();
		label_tileSize.setText("Tile Size: " + (new Vec2i(tileSet.tileW, tileSet.tileH)).toString());
	}
}
