package gui;

import io.FilterReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

import values.Value;

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
		String filterName = obj.getString("name");
		
		this.states.put(filterName, filterListener);
		
		// creating model of the filter
		FilterModel filter = new FilterModel();
		filter.setFilterName(filterName);

		JCheckBox checkBox = fp.addFilterLabel(obj.getString("title"));
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
		Map<String, Value<?>> map = model.getParams();
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		for (Entry<String, Value<?>> entry : map.entrySet()) {
			Number value = (Number) entry.getValue().getValue();
			if (value instanceof Double) {
				jsonBuilder.add(entry.getKey(), Json.createObjectBuilder()
						.add("value", value.doubleValue())
						.build());
			} else if (value instanceof Integer) {
				jsonBuilder.add(entry.getKey(), Json.createObjectBuilder()
						.add("value", value.intValue())
						.build());
			}
		}
		
		JsonObject jsonToSend = jsonBuilder.build();
		System.out.println(jsonToSend.toString());
	}	

	
}
