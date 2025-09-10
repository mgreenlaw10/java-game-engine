package src.program.editor;

import src.gui.TextArea;
import src.gui.Panel;
import src.gui.Label;
import java.awt.Insets;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.io.File;

public class LoadListPanel extends Panel implements DocumentListener, KeyListener {

	protected Label label_loadList;
	protected TextArea loadListTextArea;
	// the actual list of map names to load in-game
	protected ArrayList<String> loadList;

	final Insets INSETS = new Insets(4, 4, 4, 4);
	
	public LoadListPanel() {
		super();

		setPreferredSize(302, 160);

		loadList = new ArrayList<>();

		label_loadList = new Label("Map load list (comma separated, ENTER to confirm)");
		label_loadList.setOpaque(true);

		loadListTextArea = new TextArea(16, 16);
		loadListTextArea.setMargin(INSETS);
		loadListTextArea.setEditable(true);
		loadListTextArea.getDocument().addDocumentListener(this);
		loadListTextArea.addKeyListener(this);

		addComponent(label_loadList, 0, 0, 302, 18);
		addComponent(loadListTextArea, 0, 18, 302, 142);

		tryGetLoadListFromFile();
	}

	@Override
	public void insertUpdate(DocumentEvent e) { textChanged(); }
	@Override
	public void removeUpdate(DocumentEvent e) { textChanged(); }
	@Override
	public void changedUpdate(DocumentEvent e) { textChanged(); }

	public void textChanged() {
		String text = loadListTextArea.getText();
		parseLoadList(text);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (serializeLoadList()) {
				System.out.println("Successfully saved maps to {res/gamedata/loadList.gd}");
			} else {
				System.out.println("Cannot read load list");
			}
		}
	}

	private void parseLoadList(String list) {
		// remove any commas
		list = list.replaceAll(",", " ");
		// remove any newlines
		list = list.replaceAll("\\n", " ");
		// remove any excess whitespace
		list = list.strip();
		// split by spacing
		String[] names = list.split(" +");

		loadList.clear();
		for (String name : names) {
			loadList.add(name);
		}
	}

	private boolean serializeLoadList() {
		if (!isValidLoadList(loadList)) {
			return false;
		}
		try {
			var output = new ObjectOutputStream(new FileOutputStream("res/gamedata/map-load-list.gd"));
            output.writeObject(loadList);
            
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
        return true;
	}

	public boolean isValidLoadList(ArrayList<String> list) {
		// go to map path
		String mapPath = "res/map";
		File mapFolder = new File(mapPath);
		File[] mapFiles = mapFolder.listFiles();

		ArrayList<String> names = new ArrayList<>();
		for (File f : mapFiles) {
			names.add(f.getName());
		}
		for (String mapName : list) {
			// if there is a name in loadList that does not match a file name, return false
			if (!names.contains(mapName))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private void tryGetLoadListFromFile() {
		try {
			var input = new ObjectInputStream(new FileInputStream("res/gamedata/map-load-list.gd"));
			var list = (ArrayList<String>)input.readObject();

			if (!isValidLoadList(list)) {
				return;
			}
			String formattedNames = "";
			for (String name : list) {
				formattedNames += name + ", ";
			}
			loadListTextArea.setText(formattedNames);
		}
		catch (IOException | ClassNotFoundException e) {}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}