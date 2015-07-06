package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.io.File;
import java.io.IOException;
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
import org.openstreetmap.josm.plugins.rasterfilters.filters.Filter;
import org.openstreetmap.josm.plugins.rasterfilters.io.FilterReader;

class FiltersManager implements StateChangeListener {
	
	private Map<String, FilterStateOwner> states = new HashMap<>();
	private Map<String, JsonObject> filtersWithMeta = new HashMap<>();
	private List<String> filterTitles = new ArrayList<>();
	private Set<URL> urls = new HashSet<>();
	private ClassLoader loader;
	
	private JPanel createFilterGUI(JsonObject meta) {
		
		FilterPanel fp = new FilterPanel();
		
		//listener to track sliders and checkbox of creating filter
		FilterGuiListener filterListener = new FilterGuiListener(this);
		String filterClassName = meta.getString("classname");
		String filterTitle = meta.getString("title");
		
		states.put(filterClassName, filterListener);
		filterTitles.add(filterTitle);
		System.out.println(filterTitle);
		// creating model of the filter
		FilterModel filter = new FilterModel();
		filter.setFilterClassName(filterClassName);

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
		
		filterListener.setFilterState(filter);
//		fp.setVisible(false);
		return fp;
	}

	public List<JPanel> createFilterPanels() throws MalformedURLException {
		
		List<JPanel> filterPanels = new ArrayList<>();
		String dir = "plugins/rasterfilters/meta-inf";

		//reading metainf from file
		FilterReader fr = new FilterReader();
		
		List<JsonObject> filtersMeta = fr.readMetaInf(dir);
		
		for (JsonObject json : filtersMeta) {
			filtersWithMeta.put(json.getString("name"), json);
			
			JsonArray binaries = json.getJsonArray("binaries");
			for (int i = 0; i < binaries.size(); i++) {
				File file = new File(binaries.getString(i));
				if (file.exists()) {
					URL url = new URL("jar", "", file.toURI().toURL() + "!/");
					urls.add(url);
				}
			}
			loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), this.getClass().getClassLoader());
			filterPanels.add(createFilterGUI(json));
		}
		
		return filterPanels;
	}
	/**
	 * @param model - model that contains info about filter which was changed
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@Override
	public void filterStateChanged(FilterModel model) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		// create json msg for sending to all instances of filters
		// here we should call the method encodeJson() from model
		Filter filterToChange = null;
		
		// TODO: this is the temporary decision, need to load classes not here
		Class<?> clazz = loader.loadClass(model.getFilterClassName());
		filterToChange = (Filter) clazz.newInstance();
		
		JsonObject jsonNewState = model.encodeJson();
		
		// TODO: check if this method returns false
		if (filterToChange != null) {
			filterToChange.changeFilterState(jsonNewState);
		} else {
			Main.debug("Cannot load the class" + model.getFilterClassName());
		}
	}	
	
	public List<String> getFilterTitles() {
		return filterTitles;
	}

}
