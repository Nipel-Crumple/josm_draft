package gui;

import java.util.HashMap;
import java.util.Map;

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
	
	
}
