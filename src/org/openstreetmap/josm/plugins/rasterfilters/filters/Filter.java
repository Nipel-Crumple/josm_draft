package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;
import java.rmi.server.UID;

import org.openstreetmap.josm.plugins.rasterfilters.model.FilterStateModel;

public interface Filter {

	public BufferedImage applyFilter(BufferedImage img);

	public void setState(FilterStateModel state);

	public void setId(UID id);
}
