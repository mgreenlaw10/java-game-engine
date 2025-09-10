package src.program.gui_test;

import javax.swing.JCheckBox;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.program.editor.Editor;
import java.awt.Graphics2D;
import src.gui.*;

public class PencilCheckBox extends GUICheckBox {

	BufferedImage images;
	final int IMGW = 16;
	final int IMGH = 16;

	public PencilCheckBox() {

		super("");
		images = Editor.createFromFile("res/image/pencil_button_48p.png");
		setOpaque(true);

		setIcon(			new ImageIcon(images.getSubimage(     0,  0, IMGW, IMGH)));
		setSelectedIcon(	new ImageIcon(images.getSubimage(  IMGW,  0, IMGW, IMGH)));
		setPressedIcon(		new ImageIcon(images.getSubimage(2*IMGW,  0, IMGW, IMGH)));
	}
}