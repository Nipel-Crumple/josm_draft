package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;
import java.rmi.server.UID;

import javax.json.JsonObject;

import org.openstreetmap.josm.plugins.rasterfilters.model.FilterStateModel;

public interface Filter{
	
	public boolean changeFilterState(JsonObject filterState);
	public BufferedImage applyFilter(BufferedImage img);
	public void setState(FilterStateModel state);
	public void setId(UID id);
}
