package src.program.editor;

import src.gui.Panel;
import src.gui.ListPanel;
import src.gui.Label;
import src.gui.Button;
import src.gui.TextField;
import src.obj.Texture;
import src.obj.Teleporter;
import src.math.Vec2d;
import src.math.Vec2i;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.NumberFormatException;
import src.obj.Tile;
import src.program.editor.ui.MapListener;

public class CreateTeleporterTab extends ListPanel implements ActionListener, MapListener {

	Editor editor;

	// which map are we goin to
	protected Label label_destinationMap;
	protected MapSelectionBox destinationMapBox;
	// which tile are we goin to
	protected Label label_destinationPos;
	protected TextField posXField;
	protected TextField posYField;
	protected Button createButton;

	public CreateTeleporterTab(Editor editor) {
		super(32);
		this.editor = editor;
		editor.addMapListener(this);
		setPreferredSize(CreateObjectPanel.TAB_WIDTH, 32 * 4);
		addColDiv(96);
		Vec2i textOffset = new Vec2i(4, 0);
		setCellConfig(0, 1, 4, 4, 4, 4);
		setCellConfig(1, 1, 4, 4, 4, 4);
		setCellConfig(2, 1, 4, 4, 4, 4);

		label_destinationMap = new Label("To map:", Label.LEFT);
		label_destinationPos = new Label("To pos:", Label.LEFT);
		label_destinationMap.offset = textOffset;
		label_destinationPos.offset = textOffset;
		destinationMapBox = new MapSelectionBox(editor.getActiveMap().name);
		destinationMapBox.insertItemAt("<this>", 0);
		Label xLabel = new Label("x: ");
		Label yLabel = new Label("y: ");
		posXField = new TextField(3);
		posYField = new TextField(3);
		posXField.offset = textOffset;
		posYField.offset = textOffset;
		Panel dstPosPanel = new Panel();
		dstPosPanel.addComponent(xLabel, 	0,   0, 16, 32);
		dstPosPanel.addComponent(posXField, 16,  0, 70, 32);
		dstPosPanel.addComponent(yLabel, 	86,  0, 16, 32);
		dstPosPanel.addComponent(posYField, 102, 0, 70, 32);
		createButton = new Button("Create");
		createButton.addActionListener(this);
		
		addCell(label_destinationMap, 0, 0);
		addCell(destinationMapBox, 0, 1);
		addCell(label_destinationPos, 1, 0);
		addCell(dstPosPanel, 1, 1);
		addCell(createButton, 2, 1);
	}

	@Override
	public void mapChanged() {

		remove(destinationMapBox);
		destinationMapBox = new MapSelectionBox(editor.getActiveMap().name);
		destinationMapBox.insertItemAt("<this>", 0);
		destinationMapBox.setSelectedIndex(0);
		addCell(destinationMapBox, 0, 1);
	}

	public boolean tryCreateTeleporter() {

		String dstMapName = (String)destinationMapBox.getSelectedItem();
		if (dstMapName != null && dstMapName.equals("<this>"))
			dstMapName = null;

		String dstXStr = posXField.getText();
		String dstYStr = posYField.getText();

		double dstX;
		double dstY;
		Vec2d dstPos;
		try { // parse text fields as ints
			dstX = Double.parseDouble(dstXStr);
			dstY = Double.parseDouble(dstYStr);
			dstPos = new Vec2d(dstX, dstY);
		} 
		catch (NumberFormatException e) {
			System.out.println("Non-numbers found in numeric textfield.");
			return false;
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

		Teleporter tp = new Teleporter (
			srcTile.mapCoords,
			srcTile.tileSetCoords,
			srcTile.tileSetName,
			editor.getSelectedLayer(),
			dstMapName, 
			dstPos,
			null // map loader should hook this to game instance when the map is loaded
		);

		// for now, teleporter MUST REPLACE a tile. it cannot be placed on a null tile
		editor.getActiveMap().removeTile(srcPos.x, srcPos.y, selectedLayer);
		editor.getActiveMap().addTile(tp);
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == createButton && tryCreateTeleporter()) {
			System.out.println("Teleporter created successfully.");
		} else {
			System.out.println("Teleporter creation failed.");
		}
	}
}