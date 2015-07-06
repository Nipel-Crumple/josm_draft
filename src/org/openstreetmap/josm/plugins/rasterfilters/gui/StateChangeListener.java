package org.openstreetmap.josm.plugins.rasterfilters.gui;

public interface StateChangeListener {
	
	public void filterStateChanged(FilterModel json) throws InstantiationException, IllegalAccessException, ClassNotFoundException;
	
}
