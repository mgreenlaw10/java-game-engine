package src;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

import javax.swing.JPanel;
import java.util.function.Consumer;

import src.math.*;

public class Renderer extends JPanel {

    /* Per-Program draw method
    */
    private Consumer<Graphics2D> renderMethod;

    public Renderer() {
        this.setLayout(null);     
        this.setOpaque(true);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    /* Run renderMethod() every frame
    */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D screenBuffer = (Graphics2D)g;

        assert(renderMethod != null);
        renderMethod.accept(screenBuffer);
    }

    /* Always set in the constructor of a Program
    */
    public void defineRenderMethod(Consumer<Graphics2D> func) {
        renderMethod = func;
    }

    public Vec2i centerString(String text, int x, int y) {

        return new Vec2i( x - getGraphics().getFontMetrics().stringWidth(text) / 2,
                          y + getGraphics().getFontMetrics().getAscent() - getGraphics().getFontMetrics().getHeight() / 2 );
    }

    public void drawStringCentered(String text, int x, int y, Graphics2D g2) {

        g2.drawString( text,
                       x - g2.getFontMetrics().stringWidth(text) / 2, 
                       y + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getHeight() / 2 );
    }
}