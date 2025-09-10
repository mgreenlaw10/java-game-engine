package src.program.editor;

import java.util.ArrayList;
import java.util.HashMap;
import src.gui.Panel;
import src.gui.TabbedPanel;
import src.obj.NinePatchTexture;
import src.program.editor.ui.MapListener; 
import src.obj.Map;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class CenterPanel extends Panel implements ChangeListener {

	Editor editor;

	protected TabbedPanel mapTabPanel; 
	protected HashMap<MapRenderer, Map> renderers;

	NinePatchTexture background = Editor.PANEL_BG_TEXTURE;

	public CenterPanel(Editor editor) {

		super();
		this.editor = editor;

		mapTabPanel = new TabbedPanel();
		renderers = new HashMap<>();
		
		createNewTab(editor.getActiveMap(), true);

		setPreferredBounds(384, 32, 688, 688);
		background.setSize(688, 688);
		setBG(background);

		addComponent(mapTabPanel, 17, 17, 654, 654);

		mapTabPanel.addChangeListener(this);
	}

	public MapRenderer getMapRenderer() {
		return (MapRenderer)mapTabPanel.getSelectedComponent();
	}

	// {select}: select the new tab or not
	public void createNewTab(Map map, boolean select) {
		// if the map is null or unnamed
		MapRenderer tab = new MapRenderer(editor);
		String tabName = map.name.equals("")? "*unnamed*" : editor.getActiveMap().name;
		mapTabPanel.addTab(tabName, tab);
		if (select) {
			mapTabPanel.setSelectedComponent(tab);
		}
		// associate each renderer with its map
		renderers.put(tab, map);
	}

	public void updateSelectedTabTitle(String str) {
		mapTabPanel.setTitleAt(mapTabPanel.getSelectedIndex(), str);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		for (MapRenderer mr : renderers.keySet()) {
			if (mapTabPanel.getSelectedComponent() == mr) { 
				editor.setActiveMap(mr.mapStateManager.getCurrentMapState());
				mr.setVisible(true);
			} else {
				mr.setVisible(false);
			}
		}
	}
}