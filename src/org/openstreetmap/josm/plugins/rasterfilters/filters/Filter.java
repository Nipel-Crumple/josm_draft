package org.openstreetmap.josm.plugins.rasterfilters.filters;

import javax.json.JsonObject;

public interface Filter{
	
	public boolean changeFilterState(JsonObject newState);
}
