package gui;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import values.SliderValue;
import values.Value;

public class FilterModel {
	
	private Map<String, Value<?>> params = new HashMap<>();
	private String filterName;
	
	public FilterModel(){ 
		
	}
	
	public Map<String, Value<?>> getParams() {
		return params;
	}
	
	public String getFilterName() {
		return filterName;
	}
	
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	
	public void addParams(JsonObject json) {
		String parameterName = json.getString("name");
		String valueType = json.getString("value_type");
		
		// setting up the beginning state of filter 
		// according to his metainfo
		if (valueType.equals("float")) {
			double defaultValue =  json.getJsonNumber("default").doubleValue();
			SliderValue<Double> value = new SliderValue<>(parameterName, defaultValue);
			params.put(parameterName, value);
		} else if (valueType.equals("integer")) {
			int defaultValue = json.getJsonNumber("default").intValue();
			SliderValue<Integer> value = new SliderValue<>(parameterName, defaultValue);
			params.put(parameterName, value);
		}
	}
}
