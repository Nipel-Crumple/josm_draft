package org.openstreetmap.josm.plugins.rasterfilters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

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
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class RasterFiltersPlugin extends Plugin implements LayerChangeListener{

	public RasterFiltersPlugin(PluginInformation info) {
		super(info);
		Main.debug("Here we are");
		getLayerList();
	}
	
	public void getLayerList() {
	}
	
	@Override
	public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
		Main.debug("INITIALIZING!");
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
		LayerListDialog dialog = Main.map.getToggleDialog(LayerListDialog.class);
		ShowLayerFiltersDialog action = new ShowLayerFiltersDialog();
		((JPanel)dialog.getComponent(2)).add(new SideButton(action, false));
		Main.debug("My name is" + dialog.getClass().getCanonicalName());
		
	}

	@Override
	public void layerRemoved(Layer oldLayer) {
		Main.debug("Layer was removed AXAZAZAZ");
	}
}


