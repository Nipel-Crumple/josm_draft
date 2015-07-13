package org.openstreetmap.josm.plugins.rasterfilters.gui;

import org.openstreetmap.josm.plugins.rasterfilters.model.FilterModel;

public interface FilterStateOwner {
	
	public FilterModel getState();
	
}