package src.program.editor;

import src.gui.*;
import src.obj.Texture;
import src.math.*;
import java.util.ArrayList;

public class CreateObjectPanel extends Panel {

	Editor editor;

	protected ArrayList<Panel> tabs;
	protected Panel activeTab;
	protected CreateTeleporterTab createTeleporterTab;
	protected CreateEnemySpawnpointTab createEnemySpawnpointTab;

	String[] objectTypes = {
		"", 
		"teleporter", 
		"enemy spawn"
	};

    final Texture background = new Texture("res/image/gui/object-panel-bg.png");

    // bounds for tabs
    public static int TAB_X = 4;
    public static int TAB_Y = 4;
    public static int TAB_WIDTH = 328;

	public CreateObjectPanel(Editor pHandle) {
		super();
		editor = pHandle;
		editor.addSelectedMapTileListener(this);
		setBG(background);
		setPreferredBounds(1440, 32, 336, 688);

		tabs = new ArrayList<>();
		activeTab = null;

		createTeleporterTab = new CreateTeleporterTab(editor);
		createEnemySpawnpointTab = new CreateEnemySpawnpointTab(editor);
		tabs.add(createTeleporterTab);
		tabs.add(createEnemySpawnpointTab);
		addComponent(createTeleporterTab, TAB_X, TAB_Y);
		addComponent(createEnemySpawnpointTab, TAB_X, TAB_Y);

		updateVisibleTab();
	}

	private void updateVisibleTab() {
		for (Panel p : tabs) {
			if (p == activeTab) {
				p.setVisible(true);
			} else {
				p.setVisible(false);
			}
		}
	}

	public void setActiveTab(Panel tab) {
		activeTab = tab;
		updateVisibleTab();
	}
}