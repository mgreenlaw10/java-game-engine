package src.program.game;

import java.awt.image.BufferedImage;
import src.math.*;
import src.obj.TileSet;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.BitSet;

public class PlayerInputController implements KeyListener {

	BitSet keys;

	public static final int INPUT_UP 			= KeyEvent.VK_W;
	public static final int INPUT_DOWN 			= KeyEvent.VK_S;
	public static final int INPUT_LEFT 			= KeyEvent.VK_A;
	public static final int INPUT_RIGHT 		= KeyEvent.VK_D;

    int lastPressedMovementKey;

	public PlayerInputController() {
		keys = new BitSet(256);
		lastPressedMovementKey = INPUT_DOWN;
	}

	public boolean pressed(int key) {
		return keys.get(key);
	}

	public boolean upPressed() {
		return keys.get(INPUT_UP);
	}

	public boolean downPressed() {
		return keys.get(INPUT_DOWN);
	}

	public boolean leftPressed() {
		return keys.get(INPUT_LEFT);
	}

	public boolean rightPressed() {
		return keys.get(INPUT_RIGHT);
	}

	public int getInputDirection() {

		int dir = 0;
		dir += keys.get(INPUT_UP)? 1 : 0;
		dir += keys.get(INPUT_DOWN)? 1 : 0;
		dir += keys.get(INPUT_LEFT)? 1 : 0;
		dir += keys.get(INPUT_RIGHT)? 1 : 0;
		return dir;
	}

	public boolean moving() { 
		return upPressed() || downPressed() || leftPressed() || rightPressed();
	}

	public int getLastPressedMovementKey() {
		return lastPressedMovementKey;
	}

	public boolean isMovementKey(int key) { 
		return key == INPUT_UP ||
			   key == INPUT_DOWN ||
			   key == INPUT_LEFT ||
			   key == INPUT_RIGHT;
	}

	@Override
    public void keyPressed(KeyEvent e) {
    	keys.set(e.getKeyCode());
    	if (isMovementKey(e.getKeyCode())) {
    		lastPressedMovementKey = e.getKeyCode();
    	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	keys.clear(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}