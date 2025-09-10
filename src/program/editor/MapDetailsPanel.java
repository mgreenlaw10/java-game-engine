package src.program.editor;

import src.gui.ListPanel;
import src.gui.Label;
import java.awt.Rectangle;
import java.awt.Insets;
import src.math.Vec2i;
import src.program.editor.ui.MapListener;
import src.program.editor.ui.MapDataListener;
import src.obj.Map;

public class MapDetailsPanel extends ListPanel implements MapListener, MapDataListener {

	Editor editor;

	protected Label label_mapDetails;
	protected Label label_mapSize;
	protected Label label_minCol;
	protected Label label_minRow;

	public MapDetailsPanel(Editor editor) {
		super(16);
		setPreferredSize(144, 144);
		this.editor = editor;
		this.editor.addMapListener(this);
		this.editor.addMapDataListener(this);

		label_mapDetails = new Label("MAP DETAILS", Label.LEFT);
		label_mapSize = new Label("Size: EMPTY", Label.LEFT);
		label_minCol = new Label("Min col: NONE", Label.LEFT);
		label_minRow = new Label("Min row: NONE", Label.LEFT);

		var OFFSET = new Vec2i(4, 0);
		label_mapDetails.offset = OFFSET;
		label_mapSize.offset = OFFSET;
		label_minCol.offset = OFFSET;
		label_minRow.offset = OFFSET;

		addCell(label_mapDetails, 0);
		addCell(label_mapSize,    1);
		addCell(label_minCol,     2);
		addCell(label_minRow,     3);
	}

	// for labels
	Vec2i dimension = new Vec2i();
	Vec2i minCoords = new Vec2i();

	@Override
	public void mapChanged() {
		editor.getActiveMap().querySize(dimension, minCoords);

		updateMapSizeLabel();
		updateMinColLabel();
		updateMinRowLabel();
	}

	@Override
	public void mapEdited() {
		editor.getActiveMap().querySize(dimension, minCoords);

		updateMapSizeLabel();
		updateMinColLabel();
		updateMinRowLabel();
	}

	private void updateMapSizeLabel() {
		Map activeMap = editor.getActiveMap();

		if (activeMap.tileData.isEmpty()) {
			label_mapSize.setText("Size: EMPTY");
		} else {
			label_mapSize.setText("Map size: " + dimension.toString());
		}
	}

	private void updateMinColLabel() {
		Map activeMap = editor.getActiveMap();

		if (activeMap.tileData.isEmpty()) {
			label_minCol.setText("Min col: NONE");
		} else {
			label_minCol.setText("Min col: " + minCoords.x);
		}
	}

	private void updateMinRowLabel() {
		Map activeMap = editor.getActiveMap();

		if (activeMap.tileData.isEmpty()) {
			label_minRow.setText("Min row: NONE");
		} else {
			label_minRow.setText("Min row: " + minCoords.y);
		}
	}
}