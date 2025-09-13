package src.program.game;

import java.awt.Graphics2D;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import java.awt.geom.Rectangle2D;

import src.obj.*;
import src.program.editor.Editor;
import src.math.*;
import src.program.*;
import src.Engine;
import src.program.game.Entity.LifeState;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.io.*;

import java.lang.ClassNotFoundException;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;

public class Game extends Program implements KeyListener {

	JButton exitToMenuButton;

	Camera camera;
	Player player;
	LevelManager levelManager;
	KeyboardInput keyboardInput;
	Vec2d playerSpawnPoint = new Vec2d(0, 0);
	Dimension screenSize =  new Dimension(1200, 900); // px
	Vec2d cameraDimensions = new Vec2d(24, 18); // tiles
	int tileSize = ((int)screenSize.getWidth() / (int)cameraDimensions.x); // px
	
	public Camera getCamera() {
		return camera;
	}
	public Player getPlayer() {
		return player;
	}

	public Vec2d getCameraDimensions() {
		return cameraDimensions;
	}

	public int getTileSize() {
		return tileSize;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public void setPlayerSpawnpoint(double x, double y) {
		playerSpawnPoint.x = x;
		playerSpawnPoint.y = y;
	}
	public void spawnPlayer() {
		player.x = playerSpawnPoint.x;
		player.y = playerSpawnPoint.y;
	}

	public Game() {
 
		super (1200, 900);
		setResizable(false);
		createMenuButton();

        player = new Player(playerSpawnPoint, new Vec2d(tileSize, tileSize), this);
        camera = new Camera(player.xy(), Vec2d.VectorScale(cameraDimensions, tileSize), this);
        levelManager = new LevelManager(this);
        levelManager.loadTileSet(new TileSet("res/tileset/tileset_1.png", 16));
        keyboardInput = new KeyboardInput(this);
        tryLoadMapsFromLoadList();

        renderer.requestFocusInWindow();
        renderer.addKeyListener(player.getPlayerInputController());
        renderer.addKeyListener(this);

        spawnPlayer();
	}

	@SuppressWarnings("unchecked")
	private void tryLoadMapsFromLoadList() {
		try {
			var input = new ObjectInputStream(new FileInputStream("res/gamedata/map-load-list.gd"));
			var loadList = (ArrayList<String>)input.readObject();
			for (String mapName : loadList) {
				levelManager.loadLevelFromFile(mapName, "res/map/" + mapName);
			}
			levelManager.setCurrentLevel(levelManager.getLevels().get(0));
		} catch (IOException | ClassNotFoundException e) {}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(double delta) {
		camera.update();
		// clone in order to guarantee that changes to the entity list don't apply in the middle of an update loop
		ArrayList<Entity> activeEntities = (ArrayList<Entity>)levelManager.getCurrentLevel().entities.clone();
		for (Entity e : activeEntities) {
			if (e.getLifeState() == LifeState.ALIVE) {
		 		e.update(delta);
			}
			else if (e.getLifeState() == LifeState.DYING) {
				e.whileDying();
			}
		}
		// no need to clone because the contents of this list shouldn't ever change
		ArrayList<Box2d> activeWalls = levelManager.getCurrentLevel().walls;
		Collision.doEntityPhysics(activeEntities, activeWalls, delta);
		
		removeDeadEntities();
	}

	// at the end of every update loop, remove all entities that died during this update
	void removeDeadEntities() {
		ArrayList<Entity> buf = new ArrayList<>();
		for (Entity e : levelManager.getCurrentLevel().entities) {
			if (!e.isDead())
				buf.add(e);
			levelManager.getCurrentLevel().entities = buf;
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		camera.drawVisibleMapArea(g2);
		camera.drawVisibleEntities2(g2);
		if (drawHitboxes) {
			camera.drawWalls(g2);
			camera.drawEntityPositions(g2);
		}
		if (drawRays) {
			camera.drawRays(g2);
		}
		if (drawEntityStates) {
			camera.drawEntityStates(g2);
		}

	}

	private void createMenuButton() {

		exitToMenuButton = new JButton("<= Exit");
        exitToMenuButton.setBorder(new BevelBorder(BevelBorder.LOWERED));
        exitToMenuButton.setBounds(5, 5, 75, 25);

        exitToMenuButton.addActionListener(e ->  {
            Engine.getInstance().switchProgram(MainMenu.class);
        });

        renderer.add(exitToMenuButton);
	}

	private Map loadMap(String path) throws IOException, ClassNotFoundException { 

		Map out;

        var fStream = new FileInputStream(path);
        var oStream = new ObjectInputStream(fStream);

        out = (Map)oStream.readObject();

        oStream.close();
        fStream.close();

        return out;
    }

    boolean drawHitboxes = false;
    boolean drawRays = false;
    boolean drawEntityStates = false;

    public boolean getDrawHitboxes() {
    	return drawHitboxes;
    }
    public void setDrawHitboxes(boolean val) { 
    	drawHitboxes = val;
    }
    public void toggleDrawHitboxes() {
    	drawHitboxes = !drawHitboxes;
    }
    public boolean getDrawRays() {
    	return drawRays;
    }
    public void setDrawRays(boolean val) {
    	drawRays = val;
    }
    public void toggleDrawRays() {
    	drawRays = !drawRays;
    }
    public boolean getDrawEntityStates() {
    	return drawEntityStates;
    }
    public void setDrawEntityStates(boolean val) {
    	drawEntityStates = val;
    }
    public void toggleDrawEntityStates() { 
    	drawEntityStates = !drawEntityStates;
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}

