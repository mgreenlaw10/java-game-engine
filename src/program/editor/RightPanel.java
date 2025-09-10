package src.program.editor;

import src.gui.*;
import src.obj.TileSet;
import src.obj.Texture;
import src.obj.NinePatchTexture;
import src.math.Vec2i;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JTextField;
import src.obj.Map;
import src.obj.Tile;
import src.program.editor.ui.MapListener;

public class RightPanel extends Panel implements MapListener {

	Editor editor;

	protected MapIOPanel mapIOPanel;
	// this is just a reflection of the selected map renderer's lmp instance
	protected LayerManagementPanel layerManagementPanel;
	protected MapDetailsPanel mapDetailsPanel;
	protected ToolBar toolBar;
	protected LoadListPanel loadList;
	protected ObjectSelectionPanel objectSelectionPanel;

   	NinePatchTexture background = Editor.PANEL_BG_TEXTURE;

	final int MAP_NAME_MAX_LEN = 15;

	public RightPanel(Editor editor) {
		super();
		this.editor = editor;
		editor.addMapListener(this);

		setPreferredBounds(1088, 32, 336, 688);
		background.setSize(336, 688);
		setBG(background);

		mapDetailsPanel = new MapDetailsPanel(editor);
		objectSelectionPanel = new ObjectSelectionPanel(editor);
		mapIOPanel = new MapIOPanel(editor);
		toolBar = new ToolBar(editor);
		layerManagementPanel = editor.centerPanel.getMapRenderer().layerManagementPanel;
		loadList = new LoadListPanel();

		addComponent(mapDetailsPanel, 16, 256);
		addComponent(objectSelectionPanel, 16, 416);
		addComponent(mapIOPanel, 16, 16);
		addComponent(toolBar, 16, 196);
		addComponent(layerManagementPanel, 176, 256);
		addComponent(loadList, 16, 512);
	}

	@Override
	public void mapChanged() {
		remove(layerManagementPanel);
		layerManagementPanel = editor.centerPanel.getMapRenderer().layerManagementPanel;
		addComponent(layerManagementPanel, 176, 256);
	}
}