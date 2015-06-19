package filters;

import javax.json.JsonObject;

public interface Filter{
	
	public boolean changeFilterState(JsonObject newState);
}
