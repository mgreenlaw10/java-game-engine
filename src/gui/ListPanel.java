package src.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Component;
import src.math.Vec2i;

public class ListPanel extends Panel {

	int cellHeight;

	boolean drawBorder = true;

	public static final Color c1 = new Color(0.44f, 0.44f, 0.44f, 1.0f);
	public static final Color c2 = new Color(0.38f, 0.38f, 0.38f, 1.0f);
	public static final Color highlight = new Color(0.3f, 0.3f, 0.3f, 0.3f);

	ArrayList<Integer> colDivs;

	private record Cell(Component comp, int index) {}
	ArrayList<Cell> cells = new ArrayList<>();

	public ListPanel(int cellHeight) {
		super();
		this.cellHeight = cellHeight;
		colDivs = new ArrayList<>();
		cells = new ArrayList<>();
		cellConfigMap = new HashMap<>();
	}

	public void addCell(Component comp, int index) {
		cells.add(new Cell(comp, index));
		addComponent(comp, 0, index * cellHeight, getWidth(), cellHeight);
	}

	// ONLY IF this ListPanel has col divisions, you can add a cell to a (col, row) instead of a whole row
	public void addCell(Component comp, int rowIndex, int colIndex) {
		if (colIndex > colDivs.size())
			return;
		// get custom config, or use default if none is set
		Vec2i cell = new Vec2i(rowIndex, colIndex);
		CellConfig config = cellConfigMap.get(cell);
		if (config == null) config = DEFAULT_CONFIG;
		// get size before applying config
		int x;
		if (colIndex == 0) {
			x = 0;
		} else {
			x = colDivs.get(colIndex - 1);
		}
		int y = rowIndex * cellHeight;
		int w = getWidth() - x;
		int h = cellHeight;
		// if this is not the final col, fit to the next col
		if (colIndex <= colDivs.size() - 1) {
			w = colDivs.get(colIndex) - x;
		}
		// finally apply config
		addComponent (
			comp, 
			x + config.leftMargin(), 
			y + config.topMargin(), 
			w - config.leftMargin() - config.rightMargin(), 
			h - config.topMargin() - config.bottomMargin()
		);
	}

	/*
		CELL CONFIG
	*/
	public record CellConfig(int leftMargin, int rightMargin, int topMargin, int bottomMargin) {}
	CellConfig DEFAULT_CONFIG = new CellConfig(0, 0, 0, 0);

	public HashMap<Vec2i, CellConfig> cellConfigMap;

	public void setCellConfig(int row, int col, int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		if (row >= getNumRows() ||
			col >= getNumCols())
				return;
		Vec2i cell = new Vec2i(row, col);
		cellConfigMap.put(cell, new CellConfig(leftMargin, rightMargin, topMargin, bottomMargin));
	}

	public void addColDiv(int x) {
		colDivs.add(x);
	}

	public int getNumRows() {
		return getHeight() / cellHeight;
	}

	public int getNumCols() {
		return colDivs.size() + 1;
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);

		int i = 0;
		boolean toggle = true;
		while (i * cellHeight < getHeight()) {
			if (toggle) {
				g2.setColor(c1);
			} else {
				g2.setColor(c2);
			}
			g2.fillRect(0, i * cellHeight, getWidth(), cellHeight);
			
			toggle = !toggle;
			i++;
		}

		int j = 0;
		toggle = true;
		while (j < colDivs.size()) {
			if (toggle) {

				int w = (j + 1 < colDivs.size())? colDivs.get(j + 1) - colDivs.get(j) : getWidth() - colDivs.get(j);
				g2.setColor(highlight);
				g2.fillRect(colDivs.get(j), 0, w, getHeight());
			}
			toggle = !toggle;
			j++;
		}

		if (drawBorder) {
			g2.setColor(Color.BLACK);
			g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

}