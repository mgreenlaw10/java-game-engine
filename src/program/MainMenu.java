package src.program;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Graphics2D;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.awt.Color;

import src.Engine;
import src.program.editor.Editor;
import src.program.game.Game;
import src.program.gui_test.GUITest;

public final class MainMenu extends Program {

    JLabel titleLabel;
    JPanel programListPanel;

    JButton editorButton;
    JButton gameButton;

    final int WINDOW_MARGIN = 40;
    final int PROGRAMLIST_MARGIN = 10;

    final Color BACKGROUND_COLOR = new Color(200, 200, 200, 255);
    
    public MainMenu() {

        super(500, 500);
        setResizable(false);

        renderer.setBackground(BACKGROUND_COLOR);

        programListPanel = new JPanel();
        programListPanel.setLayout(null);

        programListPanel.setOpaque(true);
        programListPanel.setBorder(new BevelBorder(BevelBorder.LOWERED)); 
        programListPanel.setBounds (
            WINDOW_MARGIN, 
            WINDOW_MARGIN, 
            getContentPane().getWidth() - 2 * WINDOW_MARGIN, 
            getContentPane().getHeight() - 2 * WINDOW_MARGIN
        );
        renderer.add(programListPanel);

        titleLabel = new JLabel("Select a program:");
        titleLabel.setBounds (
            WINDOW_MARGIN, 
            0, 
            120, 
            40
        );
        renderer.add(titleLabel);

        editorButton = new JButton("Editor");
        editorButton.setBounds (
            PROGRAMLIST_MARGIN, 
            PROGRAMLIST_MARGIN, 
            150, 
            50
        );

        gameButton = new JButton("Game");
        gameButton.setBounds ( 
            PROGRAMLIST_MARGIN,
            PROGRAMLIST_MARGIN * 2 + 50,
            150,
            50
        );

        programListPanel.add(editorButton);
        programListPanel.add(gameButton);

        editorButton.addActionListener(e -> Engine.getInstance().switchProgram(Editor.class));
        gameButton.addActionListener(e -> Engine.getInstance().switchProgram(Game.class));
    }

    @Override
    public void update(double delta) {}

    @Override
    public void draw(Graphics2D g2) {}
}