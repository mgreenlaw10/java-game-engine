package src.program.editor;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import src.obj.Texture;
import src.obj.NinePatchTexture;
import src.obj.Map;
import src.gui.Panel;
import src.gui.ListPanel;
import src.gui.Button;
import src.gui.Label;
import src.gui.TextField;
import src.math.Vec2i;
import src.program.editor.ui.MapListener;

public class MapIOPanel extends ListPanel implements ActionListener, MapListener {

	Editor editor;
	protected Label label_mapName;
	protected TextField mapNameTextField;
	protected Button saveMapButton;
	protected Label label_foundMaps;
	protected MapSelectionBox mapSelectionComboBox;
	protected Button loadMapButton;	
	protected Button newMapButton;

	final int WIDTH  = 304;
	final int HEIGHT = 32 * 5;
	final int MAX_NAME_LEN = 15;

	public MapIOPanel(Editor editor) {
		super(32);
		this.editor = editor;
		setPreferredSize(WIDTH, HEIGHT);
		addColDiv(196);
		Vec2i textOffset = new Vec2i(4, 0);
		setCellConfig(0, 0, 4, 4, 4, 4);
		setCellConfig(1, 0, 4, 4, 4, 4);
		setCellConfig(1, 1, 4, 4, 4, 4);
		setCellConfig(2, 0, 4, 4, 4, 4);
		setCellConfig(3, 0, 4, 4, 4, 4);
		setCellConfig(3, 1, 4, 4, 4, 4);
		setCellConfig(4, 0, 4, 4, 4, 4);

		label_mapName = new Label("map name", Label.CENTER);
		mapNameTextField = new TextField(MAX_NAME_LEN);
		saveMapButton = new Button("save");
		label_foundMaps = new Label("found maps", Label.CENTER);
		mapSelectionComboBox = new MapSelectionBox();
		loadMapButton = new Button("load");
		newMapButton = new Button("new map");

		editor.addMapListener(this);
		saveMapButton.addActionListener(this);
		loadMapButton.addActionListener(this);
		newMapButton.addActionListener(this);

		addCell(label_mapName, 0, 0);
		addCell(mapNameTextField, 1, 0);
		addCell(saveMapButton, 1, 1);
		addCell(label_foundMaps, 2, 0);
		addCell(mapSelectionComboBox, 3, 0);
		addCell(loadMapButton, 3, 1);
		addCell(newMapButton, 4, 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == saveMapButton) {
			try {
				String name = mapNameTextField.getText();
				editor.getActiveMap().name = name;
                editor.saveMapAs(editor.getActiveMap(), name);
                mapSelectionComboBox.refresh();
                editor.centerPanel.updateSelectedTabTitle(name);
            }
            catch (IOException exception) {}
		}
		if (e.getSource() == loadMapButton) {
			try {
				String mapName = (String)mapSelectionComboBox.getSelectedItem();
                Map map = editor.loadMap(mapName);
                editor.setActiveMap(map);
                editor.centerPanel.createNewTab(map, true);
            }
            catch (IOException | ClassNotFoundException exception) {}
		}
		if (e.getSource() == newMapButton) {
				Map map = new Map();
				editor.setActiveMap(map);
				editor.centerPanel.createNewTab(map, true);
		}
	}

	@Override
	public void mapChanged() {
		mapNameTextField.setText(editor.getActiveMap().name);
	}
}