package src.program.editor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.io.*;
import src.program.editor.*;
import src.obj.*;
import src.gui.*;
import src.program.gui_test.*;
import src.program.editor.*;
import java.util.ArrayList;
import java.util.List;

public class ToolBar extends Panel {

	Editor editor;

	protected enum Tool {
		PENCIL,
		ERASER,
		SELECT
	}

	protected CheckBox cbPencil;
	protected CheckBox cbEraser;
	protected CheckBox cbSelect;

	protected Label label_pencilBind;
	protected Label label_eraserBind; 
	protected Label label_selectBind;

	protected Tool currentTool;

	public ToolBar(Editor editor) {
		super();
		this.editor = editor;
		setOpaque(false);

		int NUMBUTTONS = 3;
		int CHARHEIGHT = 18;
		setPreferredSize(32 * NUMBUTTONS, 32 + CHARHEIGHT);

		// set up images
		TileSet cbPencilImages = new TileSet("res/image/gui/pencil-button.png", 16, 16, 0, 0, 0, 0);
		TileSet cbEraserImages = new TileSet("res/image/gui/eraser-button.png", 16, 16, 0, 0, 0, 0);
		TileSet cbSelectImages = new TileSet("res/image/gui/select-button.png", 16, 16, 0, 0, 0, 0);

		cbPencil = new CheckBox (
			new Texture(cbPencilImages.getFrame(0, 0)), 
			new Texture(cbPencilImages.getFrame(1, 0)), 
			new Texture(cbPencilImages.getFrame(2, 0))
		);
		cbEraser = new CheckBox (
			new Texture(cbEraserImages.getFrame(0, 0)), 
			new Texture(cbEraserImages.getFrame(1, 0)), 
			new Texture(cbEraserImages.getFrame(2, 0))
		);
		cbSelect = new CheckBox (
			new Texture(cbSelectImages.getFrame(0, 0)), 
			new Texture(cbSelectImages.getFrame(1, 0)), 
			new Texture(cbSelectImages.getFrame(2, 0))
		);

		// set up item listeners
		cbPencil.addItemListener(e -> {
			if (freezeListener)
				return;
			
    		boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
    		currentTool = Tool.PENCIL;
    		deselectCBsExcept(cbPencil);
		});
		cbEraser.addItemListener(e -> {
			if (freezeListener)
				return;

    		boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
    		currentTool = Tool.ERASER;
    		deselectCBsExcept(cbEraser);
		});
		cbSelect.addItemListener(e -> {
			if (freezeListener)
				return;

    		boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
    		currentTool = Tool.SELECT;
    		deselectCBsExcept(cbSelect);
		});

		addComponent(cbPencil, 0 , 0, 32, 32);
		addComponent(cbEraser, 32, 0, 32, 32);
		addComponent(cbSelect, 64, 0, 32, 32);

		// hard code
		label_pencilBind = new Label("1");
		label_eraserBind = new Label("2");
		label_selectBind = new Label("3");
		addComponent(label_pencilBind, 0 , 32, 32, 18);
		addComponent(label_eraserBind, 32, 32, 32, 18);
		addComponent(label_selectBind, 64, 32, 32, 18);

		// default tool
		currentTool = Tool.PENCIL;
		cbPencil.setSelected(true);
		deselectCBsExcept(cbPencil);
	}

	// KEYBOARD INPUT
    public void pencilBindPressed() {
    	if (freezeListener)
			return;
    	currentTool = Tool.PENCIL;
    	cbPencil.setSelected(true);
    	deselectCBsExcept(cbPencil);
    }

    public void eraserBindPressed() {
    	if (freezeListener)
			return;
    	currentTool = Tool.ERASER;
    	cbEraser.setSelected(true);	
    	deselectCBsExcept(cbEraser);	
    }

    public void selectBindPressed() {
    	if (freezeListener)
    		return;
    	currentTool = Tool.SELECT;
    	cbSelect.setSelected(true);
    	deselectCBsExcept(cbSelect);
    }

	public static boolean drawTeleporters = true;
    public void drawTPBindPressed() {
    	if (freezeListener)
    		return;
    	drawTeleporters = !drawTeleporters;
    }

    // can't process button input while operating on state
    volatile boolean freezeListener = false;
    void deselectCBsExcept(CheckBox cb) {
    	freezeListener = true;
    	ArrayList<CheckBox> cbs = new ArrayList<>();
    	cbs.add(cbPencil);
    	cbs.add(cbEraser);
    	cbs.add(cbSelect);

    	for (CheckBox c : cbs) {
    		if (c != cb) {
    			c.setSelected(false);
    		}
    	}
    	freezeListener = false;
    }
}