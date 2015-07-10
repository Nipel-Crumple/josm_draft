package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;

import javax.json.JsonObject;

public interface Filter{
	
	public boolean changeFilterState(JsonObject newState);
	public BufferedImage applyFilter(BufferedImage img);
}
