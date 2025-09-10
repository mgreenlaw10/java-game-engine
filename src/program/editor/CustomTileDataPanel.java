package src.program.editor;

import src.obj.NinePatchTexture;
import src.obj.Map;
import src.gui.Panel;
import src.gui.ComboBox;
import src.gui.Label;
import src.math.Vec2i;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import src.program.editor.ui.SelectedTileListener;
import src.program.editor.ui.MapListener;

public class CustomTileDataPanel extends Panel implements SelectedTileListener, MapListener {

	Editor editor;

	protected Label label_selectedTileCoords;
	protected Label label_tileType;
	protected Label label_customData;
	protected TileDisplay selectedTileDisplay;
	protected ComboBox<String> customAttributesComboBox;

	NinePatchTexture background = Editor.PANEL_BG_TEXTURE;

	public CustomTileDataPanel(Editor pHandle) {
		super();
		editor = pHandle;
		editor.addSelectedTileListener(this);
		editor.addMapListener(this);
		setPreferredSize(318, 80);
		background.setSize(318, 80);
		setBG(background);

		label_tileType = new Label("Type:", Label.LEFT);
		label_selectedTileCoords = new Label("Selected Tile: NONE", Label.LEFT);
		label_customData = new Label("Custom data: ", Label.LEFT);
		selectedTileDisplay = new TileDisplay(editor.getActiveTileSet(), editor);

		customAttributesComboBox = new ComboBox<>(96, 18, Map.CUSTOM_ATTRIBUTES);
		customAttributesComboBox.setSelectedIndex(0);
		customAttributesComboBox.addItemListener(e -> {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
		        editor.getActiveMap().updateCustomData (
		            (Vec2i) editor.getSelectedTileSetTilePos().clone(),
		            (String) customAttributesComboBox.getSelectedItem()
		        );
		        editor.activateMapDataListeners();
			}
		});

		// only appear if a tile is selected
		label_customData.setVisible(false);
		customAttributesComboBox.setVisible(false);

		addComponent(label_selectedTileCoords, 8, 8, 140, 18);
		addComponent(label_customData, 8, 48, 140, 18);
		addComponent(customAttributesComboBox, 96, 48, 120, 24);
		addComponent(selectedTileDisplay, 246, 8, 64, 64);
	}

	@Override
	public void selectedTileChanged() {
		if (!editor.getSelectedTileSetTilePos().equals(Editor.INVALID_TILE)) {

			selectedTileDisplay.setTarget(editor.getSelectedTileSetTilePos());
			label_selectedTileCoords.setText("Selected tile: " + editor.getSelectedTileSetTilePos().toString());
			loadTileTypeToComboBox(editor.getSelectedTileSetTilePos());

			label_customData.setVisible(true);
			customAttributesComboBox.setVisible(true);
		}
		else {
			selectedTileDisplay.deleteTarget();
			label_selectedTileCoords.setText("Selected tile: NONE");

			label_customData.setVisible(false);
			customAttributesComboBox.setVisible(false);
		}
	}

	void loadTileTypeToComboBox(Vec2i tile) {

		for (int i = 0; i < Map.CUSTOM_ATTRIBUTES.length; i++)
			if (editor.getActiveMap().getCustomData(tile).equals(Map.CUSTOM_ATTRIBUTES[i])) 
				customAttributesComboBox.setSelectedIndex(i);
	}

	@Override
	public void mapChanged() {
		loadTileTypeToComboBox(editor.getSelectedTileSetTilePos());
	}
}