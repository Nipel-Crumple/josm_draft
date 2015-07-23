package org.openstreetmap.josm.plugins.rasterfilters;

import java.awt.Container;

import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.rasterfilters.actions.ShowLayerFiltersDialog;
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
			MapView.addLayerChangeListener(this);
		}
	}

	@Override
	public void activeLayerChange(Layer oldLayer, Layer newLayer) {
		if (!(newLayer instanceof ImageryLayer)) {
			filterButton.setEnabled(false);
		} else {
			filterButton.setEnabled(true);
		}

	}

	@Override
	public void layerAdded(Layer newLayer) {

		if (filterButton == null) {

			LayerListDialog dialog = Main.map.getToggleDialog(LayerListDialog.class);

			if (action == null) {
				action = new ShowLayerFiltersDialog();
			}

			if (newLayer instanceof ImageryLayer) {
				filterButton = new SideButton(action, false);
				filterButton.setEnabled(true);
			} else {
				filterButton = new SideButton(action, false);
				filterButton.setEnabled(false);
			}

			JPanel buttonRowPanel = (JPanel) ((JPanel)dialog.getComponent(2)).getComponent(0);
			buttonRowPanel.add(filterButton);

			Main.debug("Layer "+ newLayer.getName() + "was added");
		}

		if (newLayer instanceof ImageryLayer) {
			FiltersDialog dialog = new FiltersDialog((ImageryLayer) newLayer);
			action.addFiltersDialog(dialog);
		}

	}

	@Override
	public void layerRemoved(Layer oldLayer) {
		Main.debug("Layer "+ oldLayer.getName() + "was removed");

		if (oldLayer instanceof ImageryLayer) {
			FiltersDialog dialog = action.getDialogByLayer(oldLayer);
			((ImageryLayer) oldLayer).removeImageProcessor(dialog.fm);
			action.removeFiltersDialog(dialog);
		}

		if (Main.map.mapView.getAllLayersAsList().size() == 0) {

			Container container = filterButton.getParent();
			container.remove(filterButton);
			filterButton = null;

		}
	}


}


