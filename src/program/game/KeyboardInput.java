package src.program.game;


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

	Game gameHandle;

	public static final KeyStroke KB_DRAWHITBOXES 	= KeyStroke.getKeyStroke('1');
	public static final KeyStroke KB_ATTACK			= KeyStroke.getKeyStroke(' ');

	public KeyboardInput(Game gameHandle) {
		this.gameHandle = gameHandle;
		setBinds();
	}

	void setBinds() {

		JComponent contentPane = (JComponent)gameHandle.getContentPane();
		InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = contentPane.getActionMap();

		Action toggleDrawHitboxes = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        gameHandle.toggleDrawHitboxes();
		    }
		};
		inputMap.put(KB_DRAWHITBOXES, "drawHitboxes");
		actionMap.put("drawHitboxes", toggleDrawHitboxes);

		Action attack = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gameHandle.getPlayer().attack();
			}
		};
		inputMap.put(KB_ATTACK, "attack");
		actionMap.put("attack", attack);

	}
}