package src.program.game;

import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import src.math.*;
import src.obj.*;
import java.awt.BasicStroke;
import src.math.RayCaster.Ray2d;
import src.Renderer;

public class Camera extends Box2d {

	Game gameHandle;

	public Camera(Vec2d position, Vec2d size, Game gameHandle) {
		super(position.x, position.y, size.x, size.y);
		this.gameHandle = gameHandle;
	}

	public boolean canSee(Box2d box) {	
		return this.intersects (
			box.x,
			box.y, 
			box.width, 
			box.height
		);
	}

	// print map draw time each frame
	boolean PRINTMAPTIME = false;
	// draw map to screen
	public void drawVisibleMapArea(Graphics2D g2) {
		// debug
		long startTime = 0;
		if (PRINTMAPTIME)
			startTime = System.nanoTime();

		Map map = gameHandle.getLevelManager().getCurrentLevel().map;
		TileSet tileSet = gameHandle.getLevelManager().getTileSet();
		BufferedImage mapImg = gameHandle.getLevelManager().getCurrentLevel().flatImage;

		double scale = tileSet.tileW / (double)gameHandle.getTileSize();
		int srcX = (int)(x * scale);
		int srcY = (int)(y * scale);
		int srcW = (int)gameHandle.getCameraDimensions().x * tileSet.tileW;
		int srcH = (int)gameHandle.getCameraDimensions().y * tileSet.tileH;
		int xOffset = map.getMinCol() * tileSet.tileW;
		int yOffset = map.getMinRow() * tileSet.tileH;

		g2.drawImage (
			mapImg,
			0, 
			0, 
			(int)gameHandle.getRenderer().getWidth(), 
			(int)gameHandle.getRenderer().getHeight(), 
			-xOffset + srcX, 
			-yOffset + srcY,
			-xOffset + srcX + srcW,
			-yOffset + srcY + srcH,
			null
		);

		//  debug
		if (PRINTMAPTIME)
			System.out.println(String.format ( 
				"time to draw map: %.3f s", (System.nanoTime() - startTime) / Math.pow(10, 9)));
	}

	// draw all entities to the screen
	public void drawVisibleEntities(Graphics2D g2) {
		for (Entity e : ySortEntities()) {
			if (canSee(e)) {
				g2.drawImage ( 
					e.getAnimationPlayer().getFrame(), 
					(int)(e.x - x), 
					(int)(e.y - y), 
					(int)e.width, 
					(int)e.height, 
					null
				);
			}
		}
	}

	public void drawVisibleEntities2(Graphics2D g2) {
		for (Entity e : ySortEntities()) {
			if (canSee(e)) {
				BufferedImage animFrame = e.getAnimationPlayer().getFrame();

				double scaleFactor = gameHandle.tileSize / 16; // the number of tiles that this frame takes up
				int drawW = (int)(animFrame.getWidth() * scaleFactor);
				int drawH = (int)(animFrame.getHeight() * scaleFactor);

				// center frame around the center of the entity
				int frameCenterX = drawW / 2;
				int frameCenterY = drawH / 2;
				int entityCenterX = (int)(e.getSize().x / 2);
				int entityCenterY = (int)(e.getSize().y / 2);

				int drawX = (int)(e.x - x - frameCenterX + entityCenterX);
				int drawY = (int)(e.y - y - frameCenterY + entityCenterY);
				
				g2.drawImage (animFrame, drawX, drawY, drawW, drawH, null);

				if (gameHandle.getDrawEntityStates()) {
					Renderer.drawStringCentered("<" + e.getStateString() + ">", drawX + drawH / 2, drawY + drawH + 8, g2);
				}
			}
		}
	}

	ArrayList<Entity> ySortEntities() {

		ArrayList<Entity> result = new ArrayList<>();
		double maxY = Integer.MIN_VALUE;

		for (Entity e : gameHandle.getLevelManager().getCurrentLevel().entities) {
			
			boolean added = false;
			for (int i = 0; i < result.size(); i++) {
				if (e.y < result.get(i).y) {
					result.add(i, e);
					added = true;
					break;
				}
			}
			if (!added) 
				result.add(e);		
		}
		return result;
	}

	public void drawEntityPositions(Graphics2D g2) {
		g2.setColor(Color.GREEN);
		g2.setStroke(new BasicStroke(2));
		for (Entity e : gameHandle.getLevelManager().getCurrentLevel().entities) {
			g2.drawRect (
				(int)(e.x - x),
				(int)(e.y - y),
				(int)(e.width),
				(int)(e.height)
			);
		}
		g2.setStroke(new BasicStroke(1));
	}

	public void drawWalls(Graphics2D g2) {
		
		var map = gameHandle.getLevelManager().getCurrentLevel().map;
		var tileSet = gameHandle.getLevelManager().getTileSet();

		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(2));
		for (Box2d wall : gameHandle.getLevelManager().getCurrentLevel().walls) {
			if (canSee(wall)) {
				g2.drawRect ( 
					(int)(wall.x - x), 
					(int)(wall.y - y), 
					(int)wall.width, 
					(int)wall.height
				);
			}
		}
		g2.setStroke(new BasicStroke(1));
	}

	public void drawRays(Graphics2D g2) {
		if (RayCaster.getDrawQueue().isEmpty())
            return;
        for (Ray2d ray : RayCaster.getDrawQueue()) {
            double x1 = ray.origin.x - this.x;
            double y1 = ray.origin.y - this.y;
            
            double x2 = x1 + ray.dir.x;
            double y2 = y1 + ray.dir.y;
            /**
             * Do not draw a ray if it is offscreen
             */
            if (x1 < 0 && x2 < 0 ||
                x1 > this.width && x2 > this.width ||
                y1 < 0 && y2 < 0 ||
                y1 > this.height && y2 > this.height)
                    continue;
            
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
           
            g2.drawLine(    (int)(x1), 
                            (int)(y1), 
                            (int)(x1 + (int)ray.dir.x), 
                            (int)(y1 + ray.dir.y)
            );
        }
        RayCaster.clearDrawQueue();
	}

	public void drawEntityStates(Graphics2D g2) {

	}

	// return a box2d that is scaled by {scale} and oriented around the center of {box}
	public Box2d centerScale(Box2d box, double scale) {

		Box2d result = new Box2d(box.x, box.y, box.width * scale, box.height * scale);
		result.x += box.width / 2 - result.width / 2;
		result.y += box.height / 2 - result.height / 2;

		return result;
	}

	public void update() { 
		centerAt(gameHandle.getPlayer().center());
	}
}