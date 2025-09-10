package src.program;

import src.Renderer;

import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public abstract class Program extends JFrame implements Runnable {

    final int FPS = 60;
    
    protected Thread programThread;
    protected Renderer renderer;
    public Renderer getRenderer() {
        return renderer;
    }
    public void setRenderer(Renderer r) {
        renderer = r;
    }

    private volatile boolean dead = false;

    /* Constructor with specific window dimensions
    */
    public Program(int defaultWidth, int defaultHeight) {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(defaultWidth, defaultHeight);
        setRenderer(new Renderer());

        renderer.setLayout(null);
        renderer.setDoubleBuffered(true);
        renderer.setFocusable(true);        
        renderer.setPreferredSize(new Dimension(defaultWidth, defaultHeight));
        renderer.defineRenderMethod(this::draw);

        add(renderer);
        setContentPane(renderer);
        pack();

        centerWindow();
        // Program construction and deletion should be directly tied to visibility.
        // Also, this is necessary to be able to query the content pane in subclass constructors.
        setVisible(true);
    }

    /* Constructor with 0-size window dimensions
    */
    public Program() {
        this(0, 0);
    }

    /* Interface for Engine to start the program
    */
    public void start() {

        programThread = new Thread(this);
        programThread.start();
    }

    public void setWindowSize(int width, int height) {
        setSize(width, height);
        centerWindow();
    }

    /* Move the window so that it is centered in the current display
    */
    public void centerWindow() {

        Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (displaySize.width - getSize().width) / 2;
        int y = (displaySize.height - getSize().height) / 2;

        setLocation(x, y);
    }

    /* Each program defines its own main update and draw methods
    */
    public abstract void update(double delta);

    public abstract void draw(Graphics2D g2);

    /* Main loop
    */
    // @Override
    // public void run() {

    //     while (!dead) { 

    //         this.update();
    //         this.renderer.repaint(); // calls this

    //         try {
    //             Thread.sleep(16L);
    //         } catch (InterruptedException e) {}
    //     }
        
    //     programThread = null;
    // }

    @Override
    public void run()
    {
        final double frameInterval = Math.pow(10, 9) / FPS;
        double nextFrameTime = System.nanoTime() + frameInterval;
        double lastFrameTime = System.nanoTime();
        
        double now;
        double sleepNanos;
        double sleepMillis;
        double deltaNanos;
        double deltaSeconds;
        
        while (!dead)
        {
            now = System.nanoTime();
            deltaNanos = now - lastFrameTime;
            deltaSeconds = deltaNanos / Math.pow(10, 9);
            lastFrameTime = now;
            
            /*  Each updatePosition is immediately followed by a frame draw, so I think
                that this only works as long as I can draw frames faster than (1/FPS) seconds
            */
            update(deltaSeconds);
            renderer.repaint();
            
            try
            {
                sleepNanos = nextFrameTime - System.nanoTime();
                sleepMillis = sleepNanos / Math.pow(10, 6);
                if (sleepMillis < 0) {
                    sleepMillis = 0;
                }
                Thread.sleep((long) sleepMillis);
                nextFrameTime += frameInterval;
            }
            catch (InterruptedException e) {}
        }
        programThread = null;
    }

    public void destroy() {
        dead = true;
        dispose();
    }
}