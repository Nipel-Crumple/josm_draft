package org.openstreetmap.josm.plugins.rasterfilters.model;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.openstreetmap.josm.gui.layer.ImageProcessor;
import org.openstreetmap.josm.plugins.rasterfilters.filters.Filter;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterGuiListener;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterPanel;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterStateOwner;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FiltersDialog;

public class FiltersManager implements StateChangeListener, ImageProcessor {
	
	public Map<Filter, FilterStateOwner> states = new HashMap<>();
	public FiltersDialog dialog;
	
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
		
		// creating model of the filter
		FilterStateModel filterState = new FilterStateModel();
		filterState.setFilterClassName(filterClassName);
		
		//loading jar with filter at runtime
		Class<?> clazz;
		
		//filter for adding to map states
		Filter filterType = null;
		
		try {
			clazz = FilterInitializer.loader.loadClass(filterState.getFilterClassName());
			filterType = (Filter) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (filterType != null) {
			states.put(filterType, filterListener);
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
				filterState.addParams(temp);
				
				fp.add(component);
			}
			
		}
		fp.addDeleteButton().addActionListener(dialog);
		
		filterListener.setFilterState(filterState);
		
		return fp;
	}

	
	/**
	 * The method notifies about changes in the filter's status
	 * 
	 * @param model - model that contains info about filter which was changed
	 */
	@Override
	public void filterStateChanged(FilterStateModel model) {
		
		// create json msg for sending to all instances of filters
		// here we should call the method encodeJson() from model
		JsonObject jsonNewState = model.encodeJson();
		
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
		return null;
	}

}
