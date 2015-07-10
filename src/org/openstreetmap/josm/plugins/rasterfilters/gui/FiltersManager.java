package org.openstreetmap.josm.plugins.rasterfilters.gui;

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
import org.openstreetmap.josm.plugins.rasterfilters.io.FilterReader;

class FiltersManager implements StateChangeListener, ImageProcessor {
	
	public Map<String, FilterStateOwner> states = new HashMap<>();
	public FiltersDialog dialog;
	public Map<String, JsonObject> filtersWithMeta = new HashMap<>();
	public List<String> filterTitles = new ArrayList<>();
	public Set<URL> urls = new HashSet<>();
	public List<JsonObject> filtersMeta;
	public ClassLoader loader;
	public Filter filterType;
	
	public FiltersManager(FiltersDialog dialog) {
		this.dialog = dialog;
	}
	
	private JPanel createFilterPanel(JsonObject meta) {
		
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
			clazz = loader.loadClass(filter.getFilterClassName());
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

	public void initFilters() throws MalformedURLException {
		
//		List<JPanel> filterPanels = new ArrayList<>();
		String dir = "plugins/rasterfilters/meta-inf";

		//reading metainf from file
		FilterReader fr = new FilterReader();
		
		filtersMeta = fr.readMetaInf(dir);
		
		for (JsonObject json : filtersMeta) {
			filterTitles.add(json.getString("title"));
			filtersWithMeta.put(json.getString("name"), json);
			
			JsonArray binaries = json.getJsonArray("binaries");
			for (int i = 0; i < binaries.size(); i++) {
				File file = new File(binaries.getString(i));
				if (file.exists()) {
					URL url = new URL("jar", "", file.toURI().toURL() + "!/");
					urls.add(url);
				}
			}
		}
		loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), this.getClass().getClassLoader());
	}
	/**
	 * @param model - model that contains info about filter which was changed
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@Override
	public void filterStateChanged(FilterModel model) {
		
		// create json msg for sending to all instances of filters
		// here we should call the method encodeJson() from model
		
		JsonObject jsonNewState = model.encodeJson();
		
		// TODO: check if this method returns false
		if (filterType != null) {
			filterType.changeFilterState(jsonNewState);
		} else {
			Main.debug("Cannot load the class" + model.getFilterClassName());
		}
	}	
	
	public JPanel createPanelByTitle(String title) {
		for (JsonObject json : filtersMeta) {
			if (json.getString("title").equals(title)) {
				return createFilterPanel(json);
			}
		}
		return null;
	}
	
	public List<String> getFilterTitles() {
		return filterTitles;
	}

	@Override
	public BufferedImage process(BufferedImage image) {
		// TODO Auto-generated method stub
		return filterType.applyFilter(image);
	}

}
