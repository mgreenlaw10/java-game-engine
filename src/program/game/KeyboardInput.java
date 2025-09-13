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
	public static final KeyStroke KB_DRAWRAYS		= KeyStroke.getKeyStroke('2');
	public static final KeyStroke KB_DRAWSTATES     = KeyStroke.getKeyStroke('3');
	public static final KeyStroke KB_DRAWALL		= KeyStroke.getKeyStroke('0');

	public static final KeyStroke KB_STARTATTACK    = KeyStroke.getKeyStroke(' '); // space
	public static final KeyStroke KB_ENDATTACK		= KeyStroke.getKeyStroke("released SPACE");

	public KeyboardInput(Game gameHandle) {
		this.gameHandle = gameHandle;
		setBinds();
	}

	void setBinds() {

		JComponent contentPane = (JComponent)gameHandle.getContentPane();
		InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = contentPane.getActionMap();

		// DRAW LOOP
		Action toggleDrawHitboxes = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        gameHandle.toggleDrawHitboxes();
		    }
		};
		inputMap.put(KB_DRAWHITBOXES, "drawHitboxes");
		actionMap.put("drawHitboxes", toggleDrawHitboxes);

		Action toggleDrawRays = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gameHandle.toggleDrawRays();
			}
		};
		inputMap.put(KB_DRAWRAYS, "drawRays");
		actionMap.put("drawRays", toggleDrawRays);

		Action toggleDrawEntityStates = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gameHandle.toggleDrawEntityStates();
			}
		};
		inputMap.put(KB_DRAWSTATES, "drawEntityStates");
		actionMap.put("drawEntityStates", toggleDrawEntityStates);

		Action drawAll = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// gameHandle.setDrawHitboxes(true);
				// gameHandle.setDrawRays(true);
				// gameHandle.setDrawEntityStates(true);
				gameHandle.toggleDrawHitboxes();
				gameHandle.toggleDrawRays();
				gameHandle.toggleDrawEntityStates();
			}
		};
		inputMap.put(KB_DRAWALL, "drawAll");
		actionMap.put("drawAll", drawAll);

		// PLAYER ATTACK
		Action startAttack = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gameHandle.getPlayer().setAttacking(true);
			}
		};
		Action endAttack = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				gameHandle.getPlayer().setAttacking(false);
			}
		};
		inputMap.put(KB_STARTATTACK, "startAttack");
		actionMap.put("startAttack", startAttack);
		inputMap.put(KB_ENDATTACK, "endAttack");
		actionMap.put("endAttack", endAttack);

	}
}