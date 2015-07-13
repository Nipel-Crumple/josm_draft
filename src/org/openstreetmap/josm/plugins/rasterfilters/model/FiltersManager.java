package org.openstreetmap.josm.plugins.rasterfilters.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.ImageProcessor;
import org.openstreetmap.josm.plugins.rasterfilters.filters.Filter;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterGuiListener;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterPanel;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterStateOwner;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FiltersDialog;
import org.openstreetmap.josm.plugins.rasterfilters.io.FilterReader;

public class FiltersManager implements StateChangeListener, ImageProcessor {
	
	public Map<String, FilterStateOwner> states = new HashMap<>();
	public FiltersDialog dialog;
	public Filter filterType;
	
	public FiltersManager(FiltersDialog dialog) {
		this.dialog = dialog;
	}
	
	public FiltersManager() {
		
	}
	private JPanel createFilterWithPanel(JsonObject meta) {
		
		FilterPanel fp = new FilterPanel();
		//listener to track sliders and checkbox of creating filter
		FilterGuiListener filterListener = new FilterGuiListener(this);
		String filterClassName = meta.getString("classname");
		String filterTitle = meta.getString("title");
		
		fp.setName(filterTitle);
		
		
		states.put(filterClassName, filterListener);
		System.out.println(filterTitle);
		// creating model of the filter
		FilterModel filter = new FilterModel();
		filter.setFilterClassName(filterClassName);
		
		//loading jar with filter at runtime
		Class<?> clazz;
		try {
			clazz = FilterInitializer.loader.loadClass(filter.getFilterClassName());
			filterType = (Filter) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JCheckBox checkBox = fp.addFilterLabel(meta.getString("title"));
		checkBox.setName(meta.getString("name"));
		checkBox.addItemListener(filterListener);
		fp.add(checkBox);
		
		JsonArray controls = meta.getJsonArray("controls");
		
		for (int i = 0; i < controls.size(); i++) {
			JsonObject temp = controls.getJsonObject(i);
			JComponent component = fp.addGuiElement(temp);
			if (component != null) {
				if (component instanceof JSlider) {
					((JSlider) component).addChangeListener(filterListener);
				} else if (component instanceof JCheckBox) {
//					((JCheckBox) component).addItemListener(filterListener);
				}
				// adding parameters to the filter instance
				filter.addParams(temp);
				
				fp.add(component);
			}
			
		}
		fp.addDeleteButton().addActionListener(dialog);
		
		filterListener.setFilterState(filter);
//		fp.setVisible(false);
		return fp;
	}

	
	/**
	 * The method notifies about changes in the filter's status
	 * 
	 * @param model - model that contains info about filter which was changed
	 */
	@Override
	public void filterStateChanged(FilterModel model) {
		
		// create json msg for sending to all instances of filters
		// here we should call the method encodeJson() from model
		
		JsonObject jsonNewState = model.encodeJson();
		
		// TODO: check if this method returns false
		if (filterType != null) {
//			Main.debug("FilterType is not null; notifying about new state");
			filterType.changeFilterState(jsonNewState);
		} else {
			Main.debug("Cannot load the class" + model.getFilterClassName());
		}
	}	
	
	public JPanel createPanelByTitle(String title) {
		for (JsonObject json : FilterInitializer.filtersMeta) {
			if (json.getString("title").equals(title)) {
				return createFilterWithPanel(json);
			}
		}
		return null;
	}

	@Override
	public BufferedImage process(BufferedImage image) {
		// TODO Auto-generated method stub
		return filterType.applyFilter(image);
		
	}

}
