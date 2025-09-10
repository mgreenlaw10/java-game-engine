package src.program.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import src.*;
import src.obj.*;
import src.gui.*;
import src.program.editor.ui.*;
import src.program.*;
import src.math.*;

public class Editor extends Program {

    //-------------------------------------------------------//
    //                      RESOURCES
    //-------------------------------------------------------//

    public static final NinePatchTexture PANEL_BG_TEXTURE           = new NinePatchTexture("res/image/gui/panel-bg-9pt.png", 4, 4, 4, 4);
    public static final NinePatchTexture SUBPANEL_BG_TEXTURE        = new NinePatchTexture("res/image/gui/subpanel-bg-9pt.png", 4, 4, 4, 4);
    public static final NinePatchTexture TEXTFIELD_BG_TEXTURE       = new NinePatchTexture("res/image/gui/textfield-bg-9pt.png", 4, 4, 4, 4);
    public static final NinePatchTexture BUTTON_BG_TEXTURE          = new NinePatchTexture("res/image/gui/button-bg-9pt.png", 4, 4, 4, 4);
    public static final NinePatchTexture BUTTON_ROLLOVER_TEXTURE    = new NinePatchTexture("res/image/gui/button-rollover-9pt.png", 4, 4, 4, 4);
    public static final NinePatchTexture BUTTON_PRESSED_TEXTURE     = new NinePatchTexture("res/image/gui/button-pressed-9pt.png", 4, 4, 4, 4);

    final Color BACKGROUND_COLOR = new Color(155, 173, 183, 255);

    public static Font FONT;
    public static Font FONT24;

    //-------------------------------------------------------//
    //                     GLOBAL STATE
    //-------------------------------------------------------//

    public static final Vec2i INVALID_TILE = new Vec2i(Integer.MIN_VALUE, Integer.MIN_VALUE);

    private Map activeMap;
    private Vec2i selectedMapTilePos = INVALID_TILE.clone();
    private TileSet activeTileSet;
    private Vec2i selectedTileSetTilePos = INVALID_TILE.clone();

    // selected tile set tile
    public Vec2i getSelectedTileSetTilePos() {
        return selectedTileSetTilePos;
    }
    public void setSelectedTileSetTilePos(Vec2i pos) {
        selectedTileSetTilePos = pos;
        activateSelectedTileListeners();
    }

    // selected map tile
    public Vec2i getSelectedMapTilePos() {
        // selected tile pos should be unique to each map in editor
        return centerPanel.getMapRenderer().selectedTilePos;
    }
    public void setSelectedMapTilePos(Vec2i pos) {
        centerPanel.getMapRenderer().selectedTilePos = pos;
        activateSelectedMapTileListeners();
    }

    // map
    public Map getActiveMap() {
        return activeMap;
    }
    public void setActiveMap(Map map) {  
        activeMap = map;
        activateMapListeners();
    }

    // tile set
    public TileSet getActiveTileSet() {
        return activeTileSet;
    }
    public void setActiveTileSet(TileSet tileSet) {
        activeTileSet = tileSet;
        activateTileSetListeners();
    }

    // layer
    public int getSelectedLayer() {
        return centerPanel.getMapRenderer().layerManagementPanel.selectedLayer;
    }
    public void setSelectedLayer(int layer) {
        activateLayerListeners();
    }
    public boolean layerIsVisible(int layer) {
        return centerPanel.getMapRenderer().layerManagementPanel.layerIsVisible(layer);
    } 

    //-------------------------------------------------------//
    //                       INTERFACE
    //-------------------------------------------------------// 

    // when the SELECTED TILE CHANGES
    public ArrayList<SelectedTileListener> selectedTileListeners = new ArrayList<>();

    public void addSelectedTileListener(Object listener) {
        if (listener instanceof SelectedTileListener)
            selectedTileListeners.add((SelectedTileListener)listener);
    }
    public void activateSelectedTileListeners() {
        for (var listener : selectedTileListeners)
            listener.selectedTileChanged();
    }

    // when the ACTIVE MAP CHANGES
    public ArrayList<MapListener> mapListeners = new ArrayList<>();

    public void addMapListener(Object listener) {
        if (listener instanceof MapListener)
            mapListeners.add((MapListener)listener);
    }
    public void activateMapListeners() {
        for (var listener : mapListeners)
            listener.mapChanged();
    }

    // when the ACTIVE TILESET CHANGES
    public ArrayList<TileSetListener> tileSetListeners = new ArrayList<>();

    public void addTileSetListener(Object listener) {
        if (listener instanceof TileSetListener)
            tileSetListeners.add((TileSetListener)listener);
    }
    public void activateTileSetListeners() {
        for (var listener : tileSetListeners)
            listener.tileSetChanged();
    }

    // when the ACTIVE MAP IS EDITED
    public ArrayList<MapDataListener> mapDataListeners = new ArrayList<>();

    public void addMapDataListener(Object listener) {
        if (listener instanceof MapDataListener)
            mapDataListeners.add((MapDataListener)listener);
    }
    public void activateMapDataListeners() {
        for (var listener : mapDataListeners)
            listener.mapEdited();
    }

    // when the SELECTED MAP TILE CHANGES
    public ArrayList<SelectedMapTileListener> selectedMapTileListeners = new ArrayList<>();

    public void addSelectedMapTileListener(Object listener) { 
        if (listener instanceof SelectedMapTileListener)
            selectedMapTileListeners.add((SelectedMapTileListener)listener);
    }
    public void activateSelectedMapTileListeners() {
        for (var listener : selectedMapTileListeners)
            listener.selectedMapTileChanged();
    }

    // when the ACTIVE LAYER CHANGES
    public ArrayList<LayerListener> layerListeners = new ArrayList<>();
    public void addLayerListener(LayerListener listener) {
        layerListeners.add(listener);
    }
    public void activateLayerListeners() {
        for (LayerListener l : layerListeners) l.layerChanged();
    }

    //-------------------------------------------------------//
    //                    GUI COMPONENTS
    //-------------------------------------------------------//

    protected Panel gui;
    protected Button exitButton;
    protected TileSetRenderer tileSetRenderer;
    protected LeftPanel leftPanel;
    protected CenterPanel centerPanel;
    protected RightPanel rightPanel;
    protected CreateObjectPanel objectPanel;

    public ToolBar.Tool getSelectedTool() {
        return rightPanel.toolBar.currentTool;
    }

    public KeyboardInput keyboardInput;

    //-------------------------------------------------------//
    //                         INIT
    //-------------------------------------------------------//

    public Editor() {
        super(1792, 736);
        setResizable(false);
        gui = new Panel();
        gui.setBounds(0, 0, renderer.getWidth(), renderer.getHeight());
        gui.setBG(BACKGROUND_COLOR);

        keyboardInput = new KeyboardInput(this);

        //loadFont();
        setDefaultState();
        buildGUI();

        // hacky way to update left panel immediately since i load a tile set by default
        // can't call this until the gui is built, and can't build gui before setting defaults
        activateTileSetListeners();
        // for layer manager
        activateMapListeners();
    }

    private void loadFont() {
        try {

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            FONT   = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/vt.ttf")).deriveFont(18f);
            FONT24 = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/vt.ttf")).deriveFont(24f);
            ge.registerFont(FONT);
            ge.registerFont(FONT24);

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        //Label.defaultFont = FONT;
        //Button.defaultFont = FONT;
        //CheckBox.defaultFont = FONT;
        //ComboBox.defaultFont = FONT;
        //TextField.defaultFont = FONT;
    }

    private void setDefaultState() {
        activeMap = new Map();
        activeTileSet = new TileSet("tileset_1", "res/tileset/tileset_1.png", 16);
    }

    private void buildGUI() {
        exitButton = new Button(36, 16, "Exit");
        exitButton.addActionListener(e ->  {
            Engine.getInstance().switchProgram(MainMenu.class);
        });

        leftPanel = new LeftPanel(this);
        leftPanel.setOpaque(false);

        centerPanel = new CenterPanel(this);
        centerPanel.setOpaque(false);

        rightPanel = new RightPanel(this);
        rightPanel.setOpaque(false);

        objectPanel = new CreateObjectPanel(this);
        objectPanel.setOpaque(false);

        gui.addComponent(leftPanel);
        gui.addComponent(centerPanel);
        gui.addComponent(rightPanel);
        gui.addComponent(objectPanel);
        gui.addComponent(exitButton, 16, 8, 36, 16);
        
        renderer.add(gui);
        renderer.setVisible(true); 
        renderer.requestFocusInWindow();
    }

    //-------------------------------------------------------//
    //                      FILE IO
    //-------------------------------------------------------//

    final String MAP_DIRECTORY     = "res/map/";
    final String TILESET_DIRECTORY = "res/tileset/";

    public void saveMapAs(String name) throws IOException {

        var file      = new FileOutputStream(MAP_DIRECTORY + name);
        var mapWriter = new ObjectOutputStream(file);

        mapWriter.writeObject(activeMap);

        mapWriter.close();
        file.close();
    }

    public void saveCurrentMap() throws IOException {
        // if active map has no name, reject
        if (activeMap.name.equals("")) {
            System.out.println("Could not save active map because it is unnamed.");
            return;
        }
        var file      = new FileOutputStream(MAP_DIRECTORY + activeMap.name);
        var mapWriter = new ObjectOutputStream(file);

        mapWriter.writeObject(activeMap);

        mapWriter.close();
        file.close();

        System.out.println(String.format("Active map successfully saved to {%s}", MAP_DIRECTORY + activeMap.name));
    }

    public void saveMapAs(Map map, String name) throws IOException {

        var file      = new FileOutputStream(MAP_DIRECTORY + name);
        var mapWriter = new ObjectOutputStream(file);

        mapWriter.writeObject(map);
        mapWriter.close();
        file.close();

        System.out.println(String.format("Map successfully saved to {%s}", MAP_DIRECTORY + name));
    }

    public Map loadMap(String name) throws IOException, ClassNotFoundException { 
        if (name == null) {
            return new Map();
        }
        var file = new FileInputStream(MAP_DIRECTORY + name);
        var mapReader = new ObjectInputStream(file);
        Map ret = (Map)mapReader.readObject();
        mapReader.close();
        file.close();
        return ret;
    }

    @Override
    public void update(double delta) {}
    @Override
    public void draw(Graphics2D g2) {}
}