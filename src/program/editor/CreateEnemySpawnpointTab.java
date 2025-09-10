package src.program.editor;

import src.gui.ListPanel; 
import src.gui.Label;
import src.gui.ComboBox;
import src.gui.Button;
import src.math.Vec2i;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import src.obj.EntitySpawnpoint;
import src.obj.Tile;
import src.program.game.Slime;
import src.program.game.Entity;

public class CreateEnemySpawnpointTab extends ListPanel implements ActionListener {

	Editor editor;

	protected Label label_enemyType;
	protected ComboBox<String> enemyTypeComboBox;
	protected Button createButton;

	String[] enemyTypes = {
		"Slime"
	};

	public CreateEnemySpawnpointTab(Editor pHandle) {
		super(32);
		editor = pHandle;
		setPreferredSize(CreateObjectPanel.TAB_WIDTH, 32 * 6);
		addColDiv(96);

		Vec2i textOffset = new Vec2i(4, 0);

		setCellConfig(0, 1, 4, 4, 4, 4);
		setCellConfig(1, 1, 4, 4, 4, 4);
		setCellConfig(2, 1, 4, 4, 4, 4);

		label_enemyType = new Label("Enemy Type: ", Label.LEFT);
		label_enemyType.offset = textOffset;

		enemyTypeComboBox = new ComboBox<>(enemyTypes);

		createButton = new Button("Create");
		createButton.addActionListener(this);

		addCell(label_enemyType, 0, 0);
		addCell(enemyTypeComboBox, 0, 1);
		addCell(createButton, 1, 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == createButton && tryCreateEnemySpawnpoint()) {
			System.out.println((String)enemyTypeComboBox.getSelectedItem() + " Spawnpoint created successfully.");
		} else {
			System.out.println((String)enemyTypeComboBox.getSelectedItem() + " Spawnpoint creation failed.");
		}
	}

	public boolean tryCreateEnemySpawnpoint() {

		Class entityClass = null;
		String type = (String)enemyTypeComboBox.getSelectedItem();
		switch (type) {
			case "Slime" -> {
				entityClass = (new Slime()).getClass();
			}
		}

		int selectedLayer = editor.getSelectedLayer();
		Vec2i srcPos = editor.getSelectedMapTilePos();
		if (srcPos.equals(Editor.INVALID_TILE)) {
			System.out.println("No tile selected.");
			return false;
		}

		Tile srcTile = editor.getActiveMap().getTile(srcPos, selectedLayer);
		if (srcTile == null) {
			System.out.println("Selected tile is null.");
			return false;
		}

		EntitySpawnpoint es = new EntitySpawnpoint (
			srcTile.mapCoords, 
			srcTile.tileSetCoords,
			srcTile.tileSetName,
			selectedLayer,
			entityClass,
			null
		);
		editor.getActiveMap().removeTile(srcPos.x, srcPos.y, selectedLayer);
		editor.getActiveMap().addTile(es);

		return true;
	}
}