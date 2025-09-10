package src.program.editor;

import java.util.ArrayList;
import java.io.File;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import src.gui.ComboBox;
import java.util.Arrays;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MapSelectionBox extends ComboBox<String> {

	ArrayList<String> loadableMapList = new ArrayList<>();

	final String MAP_DIRECTORY = "res/map/";

	public MapSelectionBox(int w, int h) {
		super(w, h);
		setEditable(false);

		findLoadableMaps();
		for (String fileName : loadableMapList) {
			addItem(fileName);
		}
	}

	// use this if the component will be resized immediately anyway
	public MapSelectionBox() {
		this(1, 1);
	}
	public MapSelectionBox(String omit) {
		this(1, 1, omit);
	}

	// {omit} a specific map name from the list
	public MapSelectionBox(int w, int h, String omit) {
		super(w, h);
		setEditable(false);

		findLoadableMaps();
		for (String fileName : loadableMapList) {
			// if the name is not in {omit}, add it
			if (!fileName.equals(omit)) {
				addItem(fileName);
			}
		}
	}

	private void findLoadableMaps() {

		File mapFolder = new File(MAP_DIRECTORY);
		File[] mapFiles = mapFolder.listFiles();

		if (mapFiles != null) {

			for (File mapFile : mapFiles)
				if (mapFile.getName().endsWith(".map"))
					loadableMapList.add(mapFile.getName());
		}
	}

	// update to reflect changes in map folder
	public void refresh() {
		removeAllItems();
		loadableMapList.clear();
		findLoadableMaps();
		for (String fileName : loadableMapList) {
			addItem(fileName);
		}
	}
}