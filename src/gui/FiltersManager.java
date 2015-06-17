package gui;

import filters.Filter;
import io.FilterReader;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

class FiltersManager implements StateChangeListener {
	
	private Map<String, FilterStateOwner> states = new HashMap<>();
	
	public JPanel createFilterGUI() {
		
		FilterPanel fp = new FilterPanel();
		
		//reading metainf from file
		FilterReader fr = new FilterReader();
		String fileName = "sharpen.txt";
		JsonObject obj = fr.readMetaInf(fileName);
		
		//listener to track sliders and checkbox of creating filter
		FilterGuiListener filterListener = new FilterGuiListener(this);
		String filterClass = obj.getString("class");
		
		states.put(filterClass, filterListener);
		
		// creating model of the filter
		FilterModel filter = new FilterModel();
		filter.setFilterClass(filterClass);

		JCheckBox checkBox = fp.addFilterLabel(obj.getString("title"));
		checkBox.setName(obj.getString("name"));
		checkBox.addItemListener(filterListener);
		fp.add(checkBox);
		
		JsonArray controls = obj.getJsonArray("controls");
		
		for (int i = 0; i < controls.size(); i++) {
			JsonObject temp = controls.getJsonObject(i);
			JSlider slider = fp.addSlider(temp);
			slider.addChangeListener(filterListener);
			
			// adding parameters to the filter instance
			filter.addParams(temp);
			
			fp.add(slider);
		}
		
		filterListener.setFilterState(filter);
		
		return fp;
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
			filterToChange = (Filter) Class.forName(model.getFilterClass()).newInstance();
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
