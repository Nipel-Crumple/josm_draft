package org.openstreetmap.josm.plugins.rasterfilters.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.openstreetmap.josm.plugins.rasterfilters.values.SliderValue;
import org.openstreetmap.josm.plugins.rasterfilters.values.Value;

public class FilterStateModel {
	
	private Map<String, Value<?>> params = new HashMap<>();
	private String filterClassName;
	
	public FilterStateModel() { 
		
	}
	
	public Map<String, Value<?>> getParams() {
		return params;
	}
	
	public String getFilterClassName() {
		return filterClassName;
	}
	
	public void setFilterClassName(String filterClassName) {
		this.filterClassName = filterClassName;
	}
	
	public void addParams(JsonObject json) {
		String parameterName = json.getString("name");
		String valueType = json.getString("value_type");
		
		// setting up the beginning state of filter 
		// according to its metainfo
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
	
	public JsonObject encodeJson() {
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		for (Entry<String, Value<?>> entry : params.entrySet()) {
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
		
		return jsonBuilder.build();
	}
}
