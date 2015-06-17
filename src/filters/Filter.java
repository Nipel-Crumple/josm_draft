package filters;

import java.util.EventListener;

import javax.json.JsonObject;

public interface Filter{
	
	public boolean changeFilterState(JsonObject newState);
}
