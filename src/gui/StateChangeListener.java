package gui;

import javax.json.JsonObject;

public interface StateChangeListener {
	
	public void filterStateChanged(JsonObject json);
	
}
