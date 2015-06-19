package gui;

import filters.Filter;
import io.FilterReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;

class FiltersManager implements StateChangeListener {
	
	private Map<String, FilterStateOwner> states = new HashMap<>();
	
	private JPanel createFilterGUI(JsonObject meta) {
		
		FilterPanel fp = new FilterPanel();
		
		//listener to track sliders and checkbox of creating filter
		FilterGuiListener filterListener = new FilterGuiListener(this);
		String filterClass = meta.getString("class");
		
		states.put(filterClass, filterListener);
		
		// creating model of the filter
		FilterModel filter = new FilterModel();
		filter.setFilterClass(filterClass);

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
			URL url = new URL("jar:file:lib/unsharpmask.jar!/");
			ClassLoader loader = URLClassLoader.newInstance(new URL[] {url});
			Class<?> clazz = loader.loadClass("filters.UnsharpMaskFilter");
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonObject jsonNewState = model.encodeJson();
		
		// TODO: check if this method returns false
		filterToChange.changeFilterState(jsonNewState);
	}	

}
