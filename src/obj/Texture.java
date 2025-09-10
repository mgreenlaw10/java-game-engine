package src.obj;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import src.obj.ui.Drawable;

public class Texture implements Drawable {

	public BufferedImage image;

	public Texture(String fPath) {
		try { image = ImageIO.read(new File(fPath)); }
		catch (IOException e) { e.printStackTrace(); }
	}

	public Texture(BufferedImage image) {
		this.image = image;
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}
}