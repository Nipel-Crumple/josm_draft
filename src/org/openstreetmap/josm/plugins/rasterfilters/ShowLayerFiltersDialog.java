package org.openstreetmap.josm.plugins.rasterfilters;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.Layer.LayerAction;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FiltersDialog;
import org.openstreetmap.josm.tools.CheckParameterUtil;
import org.openstreetmap.josm.tools.ImageProvider;

import static org.openstreetmap.josm.tools.I18n.tr;

public final class ShowLayerFiltersDialog extends AbstractAction implements LayerAction {
    private transient Layer layer;

    private FiltersDialog dialog = new FiltersDialog();
    /**
     * Creates a {@link ShowLayerFiltersDialog} which allows to choose and
     * apply it to the layer
     *
     * @param layer  the layer. Must not be null.
     * @throws IllegalArgumentException if layer is null
     */
    public ShowLayerFiltersDialog(Layer layer) {
        this();
        putValue(NAME, tr("Filters"));
        CheckParameterUtil.ensureParameterNotNull(layer, "layer");
        this.layer = layer;
    }

    /**
     * Creates a {@link ShowHideLayerAction} which will toggle the visibility of
     * the currently selected layers
     *
     */
    public ShowLayerFiltersDialog() {
        putValue(NAME, tr("Filters"));
        putValue(SHORT_DESCRIPTION, tr("Choose filter"));
        putValue(SMALL_ICON, ImageProvider.get("dialogs/layerlist", "filters"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
    	try {
			dialog.createAndShowGUI();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

    @Override
    public boolean supportLayers(List<Layer> layers) {
        return true;
    }

    @Override
    public Component createMenuComponent() {
        return new JMenuItem(this);
    }
}