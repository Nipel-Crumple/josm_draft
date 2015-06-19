package gui;

import filters.Filter;
import io.FilterReader;

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

class FiltersManager implements StateChangeListener {
	
	private Map<String, FilterStateOwner> states = new HashMap<>();
	private Map<String, JsonObject> filtersWithMeta = new HashMap<>();
	private Set<URL> urls = new HashSet<>();
	private ClassLoader loader;
	
	private JPanel createFilterGUI(JsonObject meta) {
		
		FilterPanel fp = new FilterPanel();
		
		//listener to track sliders and checkbox of creating filter
		FilterGuiListener filterListener = new FilterGuiListener(this);
		String filterClassName = meta.getString("classname");
		
		states.put(filterClassName, filterListener);
		
		// creating model of the filter
		FilterModel filter = new FilterModel();
		filter.setFilterClass(filterClassName);

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
		
		return fp;
	}

	public List<JPanel> createFilterPanels() {
		
		List<JPanel> filterPanels = new ArrayList<>();
		String dir = "meta-inf";

		//reading metainf from file
		FilterReader fr = new FilterReader();
		
		List<JsonObject> filtersMeta = fr.readMetaInf(dir);
		
		for (JsonObject json : filtersMeta) {
			filtersWithMeta.put(json.getString("name"), json);
			
			JsonArray binaries = json.getJsonArray("binaries");
			for (int i = 0; i < binaries.size(); i++) {
				try {
					urls.add(new URL("jar:file:" + binaries.getString(i) + "!/"));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			loader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
			filterPanels.add(createFilterGUI(json));
		}
		
		return filterPanels;
	}
	/**
	 * @param model - model that contains info about filter which was changed
	 */
	@Override
	public void filterStateChanged(FilterModel model) {
		// create json msg for sending to all instances of filters
		// here we should call the method encodeJson() from model
		Filter filterToChange = null;
		try {
			
			Class<?> clazz = loader.loadClass(model.getFilterClassName());
			filterToChange = (Filter) clazz.newInstance();
//			filterToChange = (Filter) Class.forName(model.getFilterClass()).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonObject jsonNewState = model.encodeJson();
		
		// TODO: check if this method returns false
		filterToChange.changeFilterState(jsonNewState);
	}	

}
