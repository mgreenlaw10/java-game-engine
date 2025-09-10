package src.program.editor;

import src.gui.Panel;
import src.gui.Label;
import src.gui.ComboBox;
import src.obj.NinePatchTexture;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import src.program.editor.ui.MapListener;
import src.program.editor.ui.MapDataListener;
import src.program.editor.ui.SelectedMapTileListener;
import src.program.editor.ui.LayerListener;
import src.math.Vec2i;
import src.obj.Tile;

import java.util.HashMap;
import java.util.ArrayList;

public class ObjectSelectionPanel extends Panel implements ActionListener, MapDataListener, MapListener, SelectedMapTileListener, LayerListener {
	Editor editor;

	NinePatchTexture background = Editor.PANEL_BG_TEXTURE;

	protected Label label_selectedMapTileCoords;
	protected Label label_tileType;
	protected TileDisplay selectedTileDisplay;

	protected Label label_createNew;
	protected ComboBox<String> objectsBox;
	
	public ObjectSelectionPanel(Editor pHandle) { 
		super();
		editor = pHandle;
		editor.addMapListener(this);
		editor.addMapDataListener(this);
		editor.addSelectedMapTileListener(this);
		editor.addLayerListener(this);

		setPreferredSize(302, 80);
		background.setSize(302, 80);
		setBG(background);

		label_selectedMapTileCoords = new Label("Selected Tile: NONE", Label.LEFT);
		label_tileType = new Label("Tile type: NONE", Label.LEFT);
		selectedTileDisplay = new TileDisplay(editor.getActiveTileSet(), editor);

		addComponent(label_selectedMapTileCoords, 8, 8, 140, 18);
		addComponent(label_tileType, 8, 28, 140, 18);
		addComponent(selectedTileDisplay, 230, 8, 64, 64);

		buildObjectsBox();
	}

	private void buildObjectsBox() {

		label_createNew = new Label("Create New:", Label.LEFT);
		String[] objectTypes = {
			"", 
			"Teleporter", 
			"Enemy Spawnpoint"
		};
		objectsBox = new ComboBox<>(136, 24, objectTypes);
		objectsBox.addActionListener(this);

		// invisible by default, will appear when a map tile is selected
		label_createNew.setVisible(false);
		objectsBox.setVisible(false);
		addComponent(label_createNew, 8, 48, 140, 18);
		addComponent(objectsBox, 96, 48, 120, 24);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String selection = (String)objectsBox.getSelectedItem();
		if (selection.equals("Teleporter")) {
			editor.objectPanel.setActiveTab(editor.objectPanel.createTeleporterTab);
		}
		else if (selection.equals("Enemy Spawnpoint")) {
			editor.objectPanel.setActiveTab(editor.objectPanel.createEnemySpawnpointTab);
		}
		else {
			editor.objectPanel.setActiveTab(null);
		}
	}

	@Override
	public void mapEdited() {
		updateCreateNewPanel();
		updateSelectedTileDisplay();
		updateTileTypeLabel();
	}

	@Override
	public void mapChanged() {
		updateCreateNewPanel();
		updateSelectedTileDisplay();
		updateSelectedTileLabel();
		updateTileTypeLabel();
	}

	@Override
	public void selectedMapTileChanged() {
		updateCreateNewPanel();
		updateSelectedTileDisplay();
		updateSelectedTileLabel();
		updateTileTypeLabel();
	}

	@Override
	public void layerChanged() {
		updateSelectedTileDisplay();
		updateCreateNewPanel();
		updateTileTypeLabel();
	}

	private void updateTileTypeLabel() {
		Vec2i selectedMapTilePos = editor.getSelectedMapTilePos();
		Tile selectedTile = editor.getActiveMap().getTile(selectedMapTilePos);
		if (selectedTile == null) {
			label_tileType.setText("Tile Type: NONE");
			return;
		}
		String tileType = editor.getActiveMap().getCustomData(selectedTile.tileSetCoords);
		label_tileType.setText("Tile Type: " + tileType);
	}

	private void updateCreateNewPanel() {

		Vec2i selectedMapTilePos = editor.getSelectedMapTilePos();
		int selectedLayer = editor.getSelectedLayer();
		if (selectedMapTilePos.equals(Editor.INVALID_TILE) || 
			editor.getActiveMap().getTile(selectedMapTilePos, selectedLayer) == null) {

				label_createNew.setVisible(false);
				objectsBox.setVisible(false);
		} else {

			label_createNew.setVisible(true);
			objectsBox.setVisible(true);
		}
	}

	private void updateSelectedTileDisplay() {

		Vec2i selectedTilePos = editor.getSelectedMapTilePos();
		int selectedLayer = editor.getSelectedLayer();
		
		if (selectedTilePos.equals(Editor.INVALID_TILE) ||
			editor.getActiveMap().getTile(selectedTilePos, selectedLayer) == null) {
				selectedTileDisplay.deleteTarget();
		}
		else {
			Tile selectedTile = editor.getActiveMap().getTile(selectedTilePos, selectedLayer);
			selectedTileDisplay.setTarget(selectedTile.tileSetCoords);
		}
	}

	private void updateSelectedTileLabel() {

		Vec2i selectedTilePos = editor.getSelectedMapTilePos();
		if (!selectedTilePos.equals(Editor.INVALID_TILE)) {
			label_selectedMapTileCoords.setText("Selected Tile: " + selectedTilePos.toString());
		}
		else {
			label_selectedMapTileCoords.setText("Selected Tile: NONE");
		}
	}
}