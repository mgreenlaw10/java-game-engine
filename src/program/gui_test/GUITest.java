package src.program.gui_test;

import src.Engine;
import src.Renderer;
import src.program.*;
import src.gui.*;
import src.obj.*;
import src.program.editor.Editor;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import src.math.Vec2i;


public class GUITest extends Program {

	Panel p;
	Panel centerPanel;
	Panel leftPanel;
	Panel rightPanel;

	Panel mapInfoPanel;

	Texture lpImage = new Texture("res/image/gui/left-sidebar.png");
	Texture cpImage = new Texture("res/image/gui/map-renderer-bg.png");
	Texture rpImage = new Texture("res/image/gui/right-sidebar-bg.png");
	Texture miImage = new Texture("res/image/gui/map-info-panel-bg.png");

	Button exitButton;
	Button saveButton;
	Button loadButton;

	final Vec2i b4Size = new Vec2i(36, 16);
	final TileSet exitButtonTextures = new TileSet("res/image/gui/exit-button.png", b4Size.x, b4Size.y, 0, 0, 0, 0);
	final TileSet saveButtonTextures = new TileSet("res/image/gui/save-button.png", b4Size.x, b4Size.y, 0, 0, 0, 0);
	final TileSet loadButtonTextures = new TileSet("res/image/gui/load-button.png", b4Size.x, b4Size.y, 0, 0, 0, 0);

	public GUITest() {

		super (1280, 720);
		p = new Panel();	
		p.setBounds(0, 0, renderer.getWidth(), renderer.getHeight());
		p.setBG(new Color(155, 173, 183, 255));
		createMenuButton();

		// leftPanel = new Panel();
		// centerPanel = new Panel();
		// rightPanel = new Panel();

		// leftPanel.setOpaque(false);
		// leftPanel.setBG(lpImage);
		// centerPanel.setOpaque(false);
		// centerPanel.setBG(cpImage);
		// rightPanel.setOpaque(false);
		// rightPanel.setBG(rpImage);

		// mapInfoPanel = new Panel();
		// mapInfoPanel.setOpaque(false);
		// mapInfoPanel.setBG(miImage);
		
		// p.addComponent(centerPanel, 384, 16, 688, 688);
		// p.addComponent(leftPanel, 16, 16, 352, 688);
		// p.addComponent(rightPanel, 1088, 16, 176, 688);

		// loadButton = new Button();
		// saveButton = new Button();

		// rightPanel.addComponent(mapInfoPanel, 16, 16, 144, 144);
		// mapInfoPanel.addComponent(saveButton, 4, 32, b4Size.x, b4Size.y);
		// mapInfoPanel.addComponent(loadButton, 4, 64, b4Size.x, b4Size.y);


		renderer.add(p);
		setVisible(true); 
        pack();
	}

	@Override
	public void update(double delta) {}

	@Override
	public void draw(Graphics2D g2) {
		p.repaint();
	}

	private void createMenuButton() {
		

		// exitButton = new Button();
        // exitButton.addActionListener(e ->  {
        //     Engine.getInstance().switchProgram(MainMenu.class);
        // });
        // p.addComponent(exitButton, 16, 0, b4Size.x, b4Size.y);
	}
}