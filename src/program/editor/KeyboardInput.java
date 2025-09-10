package src.program.editor;

import javax.swing.SwingUtilities;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import java.awt.Container;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.IOException;

public class KeyboardInput {

	Editor editor;
	
	// toolbar tool
	public static final KeyStroke KB_SELECT_PENCIL_TOOL 	= KeyStroke.getKeyStroke('1');
	public static final KeyStroke KB_SELECT_ERASER_TOOL 	= KeyStroke.getKeyStroke('2');
	public static final KeyStroke KB_SELECT_SELECT_TOOL 	= KeyStroke.getKeyStroke('3');
	// draw toggles
	public static final KeyStroke KB_DRAW_TELEPORTERS 		= KeyStroke.getKeyStroke('T');
	// undo/redo
	public static final KeyStroke KB_UNDO 					= KeyStroke.getKeyStroke("control Z");
	public static final KeyStroke KB_REDO 					= KeyStroke.getKeyStroke("control Y");
	// save map
	public static final KeyStroke KB_SAVEMAP				= KeyStroke.getKeyStroke("control S");
	// swap layer
	public static final KeyStroke KB_LAYER0					= KeyStroke.getKeyStroke("shift 1");
	public static final KeyStroke KB_LAYER1					= KeyStroke.getKeyStroke("shift 2");
	public static final KeyStroke KB_LAYER2					= KeyStroke.getKeyStroke("shift 3");
	public static final KeyStroke KB_LAYER3					= KeyStroke.getKeyStroke("shift 4");
	// arbitrary debug action
	public static final KeyStroke KB_DEBUG 					= KeyStroke.getKeyStroke('q'); 

	public KeyboardInput(Editor editor) {
		this.editor = editor;
		setBinds();
	}

	void setBinds() {
		JComponent contentPane = (JComponent)editor.getContentPane();
		InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = contentPane.getActionMap();

		/*
			TOOL BAR
		*/
		Action selectPencilTool = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        editor.rightPanel.toolBar.pencilBindPressed();
		    }
		};
		inputMap.put(KB_SELECT_PENCIL_TOOL, "selectPencil");
		actionMap.put("selectPencil", selectPencilTool);

		Action selectEraserTool = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
		        editor.rightPanel.toolBar.eraserBindPressed();
		    }
		};
		inputMap.put(KB_SELECT_ERASER_TOOL, "selectEraser");
		actionMap.put("selectEraser", selectEraserTool);

		Action selectSelectTool = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
		        editor.rightPanel.toolBar.selectBindPressed();
		    }
		};
		inputMap.put(KB_SELECT_SELECT_TOOL, "selectSelect");
		actionMap.put("selectSelect", selectSelectTool);

		Action toggleDrawTeleporters = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				editor.rightPanel.toolBar.drawTeleporters = !editor.rightPanel.toolBar.drawTeleporters;
			}
		};
		inputMap.put(KB_DRAW_TELEPORTERS, "drawTeleporters");
		actionMap.put("drawTeleporters", toggleDrawTeleporters);
		/*
			MAP STATE MANAGER
		*/
		Action undo = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
		        	editor.centerPanel.getMapRenderer().mapStateManager.undo();
				});
		    }
		};
		inputMap.put(KB_UNDO, "undo");
		actionMap.put("undo", undo);

		Action redo = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
		        	editor.centerPanel.getMapRenderer().mapStateManager.redo();
				});
		    }
		};
		inputMap.put(KB_REDO, "redo");
		actionMap.put("redo", redo);
		/*
			SAVE MAP
		*/
		Action saveMap = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					editor.saveCurrentMap();
				}
				catch (IOException exception) {}
			}
		};
		inputMap.put(KB_SAVEMAP, "saveMap");
		actionMap.put("saveMap", saveMap);
		/*
			SWITCH ACTIVE LAYER
		*/
		// very very strange bug if this was instead editor.centerPanel.getMapRenderer().layerManagementPanel
		Action activateLayer0 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
					editor.rightPanel.layerManagementPanel.instantLayerSwitch(0);
				});
			}
		};
		Action activateLayer1 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
					editor.rightPanel.layerManagementPanel.instantLayerSwitch(1);
				});
			}
		};
		Action activateLayer2 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
					editor.rightPanel.layerManagementPanel.instantLayerSwitch(2);
				});
			}
		};
		Action activateLayer3 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
					editor.rightPanel.layerManagementPanel.instantLayerSwitch(3);
				});
			}
		};
		inputMap.put(KB_LAYER0, "layer0");
		inputMap.put(KB_LAYER1, "layer1");
		inputMap.put(KB_LAYER2, "layer2");
		inputMap.put(KB_LAYER3, "layer3");
		actionMap.put("layer0", activateLayer0);
		actionMap.put("layer1", activateLayer1);
		actionMap.put("layer2", activateLayer2);
		actionMap.put("layer3", activateLayer3);

		/*
			DEBUG
		*/
		Action debug = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
					System.out.println(editor.getActiveMap());
			}
		};
		inputMap.put(KB_DEBUG, "debug");
		actionMap.put("debug", debug);
	}
}