package org.openstreetmap.josm.plugins.rasterfilters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapFrameListener;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FiltersDialog;

public class RasterFiltersPlugin extends Plugin implements LayerChangeListener{
	
	private SideButton filterButton;
	private ShowLayerFiltersDialog action;

	public RasterFiltersPlugin(PluginInformation info) {
		super(info);
		Main.debug("Loading RasterFiltersPlugin");
	}
	
	@Override
	public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
		Main.debug("Initialising RasterFiltersPlugin in mapFrame!");
		if (Main.isDisplayingMapView()) {
			Main.map.mapView.addLayerChangeListener(this);
		}
	}

	@Override
	public void activeLayerChange(Layer oldLayer, Layer newLayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layerAdded(Layer newLayer) {
		
		if (filterButton == null) {
			
			LayerListDialog dialog = Main.map.getToggleDialog(LayerListDialog.class);
			action = new ShowLayerFiltersDialog();
			
			if (newLayer instanceof ImageryLayer) {
				filterButton = new SideButton(action, false);
				filterButton.setEnabled(true);
			} else {
				filterButton = new SideButton(action, false);
				filterButton.setEnabled(false);
			}
			
			((JPanel)dialog.getComponent(2)).add(filterButton);
			Main.debug("My name is" + dialog.getClass().getCanonicalName());
		}
		
		if (newLayer instanceof ImageryLayer) {
			FiltersDialog dialog = new FiltersDialog((ImageryLayer) newLayer);
			action.addFiltersDialog(dialog);
		}
		
	}

	@Override
	public void layerRemoved(Layer oldLayer) {
		Main.debug("Layer"+ oldLayer.getName() + "was removed");
		
		for (FiltersDialog dialog : action.dialogs) {
			
			if (dialog.activeLayer.equals(oldLayer)) {
				action.removeFiltersDialog(dialog);
			}
			
		}
	}
	
	
}


