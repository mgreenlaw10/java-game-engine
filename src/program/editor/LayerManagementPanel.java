package src.program.editor;

import src.gui.Panel;
import src.gui.ListPanel;
import src.gui.Label;
import src.gui.CheckBox;
import src.gui.Button;
import src.math.Vec2i;
import java.util.ArrayList;
import src.program.editor.ui.MapListener;
import src.obj.Texture;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class LayerManagementPanel extends ListPanel implements MapListener { 

	Editor editor;

	protected int selectedLayer;
	protected Panel titlePanel;
	protected Panel icon_visible;
	protected Panel icon_selected;

	final Texture visibleIcon = new Texture("res/image/gui/layer-visible-icon.png");
	final Texture selectedIcon = new Texture("res/image/gui/layer-selected-icon.png");
	final Vec2i TEXT_OFFSET = new Vec2i(4, 0);

	private class Layer {
		public int order;
		public boolean selected;
		public boolean visible;

		public Layer(int order, boolean selected, boolean visible) {
			this.order = order;
			this.selected = selected;
			this.visible = visible;
		}
	}

	private class LayerInterface extends Panel { 
		
		public Layer layer;
		public Label label_layerName;
		public CheckBox selectLayerCheckBox;
		public CheckBox visibilityCheckBox;

		final int W = 144;
		final int H = 16;
		final int CORRECTION = 1;

		public LayerInterface(Layer layer) {
			super();
			this.layer = layer;
			setPreferredSize(W, H);

			label_layerName = new Label("layer_" + layer.order, Label.LEFT);
			label_layerName.offset = TEXT_OFFSET;
			addComponent(label_layerName, 0, 0, getWidth(), getHeight());

			visibilityCheckBox = new CheckBox();
			visibilityCheckBox.setSelected(layer.visible);
			visibilityCheckBox.addItemListener(e -> {
				layer.visible = visibilityCheckBox.getModel().isSelected();
			});
			addComponent(visibilityCheckBox, col2X + margin + CORRECTION, margin, H, getHeight());
			
			selectLayerCheckBox = new CheckBox();
			selectLayerCheckBox.setSelected(layer.selected);
			selectLayerCheckBox.addItemListener(e -> {
				// it is implied that any time this check box is clicked, it is being selected, not deselected
				if (freezeListener)
					return;
				makeUniquelySelected(this);
			});
			addComponent(selectLayerCheckBox, col1X + margin + CORRECTION, margin, H, getHeight());
		}
	}

	private class AddLayerPanel extends Panel {

		Editor editor;
		Button newLayerButton;

		final int W = 144;
		final int H = 16;

		public AddLayerPanel(Editor editor) {
			super();
			this.editor = editor;
			setPreferredSize(W, H);
			newLayerButton = new Button(H, H, "+");
			addComponent(newLayerButton, col2X + margin, margin, H, H);

			newLayerButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editor.getActiveMap().numLayers++;
					editor.activateMapDataListeners();
					refreshPanels();
				}
			});
		}
	}

	public ArrayList<LayerInterface> layerInterfaces;
	public Label label_layers;

	static final int col1X = 96;
	static final int col2X = 120;
	static final int margin = 4; 

	public LayerManagementPanel(Editor editor) {
		super(24);
		this.editor = editor;
		this.editor.addMapListener(this);
		setPreferredSize(144, 144);
		addColDiv(col1X);
		addColDiv(col2X);

		selectedLayer = 0;
		layerInterfaces = new ArrayList<>();

		refreshPanels();
	}

	private void addTitlePanel() {
		titlePanel = new Panel();
		titlePanel.setPreferredSize(144, 16);

		icon_visible = new Panel();
		icon_selected = new Panel();
		icon_visible.setBG(visibleIcon);
		icon_selected.setBG(selectedIcon);

		label_layers = new Label("LAYERS", Label.LEFT);
		label_layers.offset = TEXT_OFFSET;

		titlePanel.addComponent(label_layers, 0, 0, titlePanel.getWidth(), titlePanel.getHeight());
		titlePanel.addComponent(icon_selected, col1X + margin, margin, 16, titlePanel.getHeight());
		titlePanel.addComponent(icon_visible, col2X + margin, margin, 16, titlePanel.getHeight());

		addCell(titlePanel, 0);
	}

	public void addLayer(int order, boolean selected, boolean visible) {

		var layerInterface = new LayerInterface(new Layer(order, selected, visible));
		layerInterfaces.add(layerInterface);
		if (selected) { 
			layerInterface.selectLayerCheckBox.setEnabled(false);
		} else {
			layerInterface.selectLayerCheckBox.setEnabled(true);
		}
		// offset by 1 from title panel
		addCell(layerInterface, order + 1);
	}

	public Layer getLayer(int order) {
		return layerInterfaces.get(order).layer;
	}

	@Override
	public void mapChanged() {
		//refreshPanels();
	}

	private void refreshPanels() {
		removeAll();
		addTitlePanel();
		layerInterfaces.clear();

		for (int i = 0; i < editor.getActiveMap().numLayers; i++) {
			if (i == selectedLayer) {
				addLayer(i, true, true);
			} else {
				addLayer(i, false, true);
			}
		}
		addCell(new AddLayerPanel(editor), editor.getActiveMap().numLayers + 1);
	}

	// to stop concurrent modification
	volatile boolean freezeListener = false;
    void deselectCBsExcept(CheckBox cb) {

    	freezeListener = true;
    	ArrayList<CheckBox> cbs = new ArrayList<>();
    	for (LayerInterface l : layerInterfaces) {
    		cbs.add(l.selectLayerCheckBox);
    	}

    	for (CheckBox c : cbs) {
    		if (c != cb) {
    			c.setSelected(false);
    		}
    	}
    	freezeListener = false;
    }

    void makeUniquelySelected(LayerInterface layerInterface) {
    	freezeListener = true;

    	for (LayerInterface l : layerInterfaces) {
    		l.selectLayerCheckBox.setSelected(false);
    		l.selectLayerCheckBox.setEnabled(true);
    		l.layer.selected = false;
    	}
    	layerInterface.selectLayerCheckBox.setSelected(true);
    	layerInterface.selectLayerCheckBox.setEnabled(false);
    	layerInterface.layer.selected = true;

    	selectedLayer = layerInterface.layer.order;
    	editor.activateLayerListeners();

    	freezeListener = false;
    }

    // call when switching the layer from anywhere outside of this panel's checkboxes,
    // hence why its necessary to bounds check {order}. currently only used in keybinds
    public void instantLayerSwitch(int order) {
    	if (order >= layerInterfaces.size()) {
    		return;
    	}
    	makeUniquelySelected(layerInterfaces.get(order));
    }

	public boolean layerIsVisible(int order) {
		for (LayerInterface l : layerInterfaces) {
			if (l.layer.order == order && l.layer.visible)
				return true;
		}
		return false;
	}

}