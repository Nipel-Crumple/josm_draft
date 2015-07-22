package org.openstreetmap.josm.plugins.rasterfilters.actions;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog.ShowHideLayerAction;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.Layer.LayerAction;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FiltersDialog;
import org.openstreetmap.josm.tools.CheckParameterUtil;
import org.openstreetmap.josm.tools.ImageProvider;

public final class ShowLayerFiltersDialog extends AbstractAction implements LayerAction {
	
	public List<FiltersDialog> dialogs = new ArrayList<>();

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
    
    public void addFiltersDialog(FiltersDialog dialog) {
    	dialogs.add(dialog);
    }
    
    public void removeFiltersDialog(FiltersDialog dialog) {
    	dialogs.remove(dialog);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	Layer layer = Main.map.mapView.getActiveLayer();
    	
    	if (layer instanceof ImageryLayer) {
    		for (FiltersDialog temp : dialogs) {
    			
    			if (temp.layer.equals(layer)) {
    				try {
    					
						temp.createAndShowGUI();
						
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
    				
    				break;
    			}
    			
    		}
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