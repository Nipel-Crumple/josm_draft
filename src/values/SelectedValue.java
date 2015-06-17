package values;

public class SelectedValue<T> implements Value<T> {

	private T selectedItem;
	private String parameterName;
	
	public SelectedValue(T value, String parameterName) {
		this.selectedItem = value;
		this.parameterName = parameterName;
	}
	
	@Override
	public T getValue() {
		return selectedItem;
	}

	@Override
	public void setValue(T value) {
		this.selectedItem = value;
	}

	@Override
	public String getParameterName() {
		return parameterName;
	}

	@Override
	public void setParameterName(String name) {
		this.parameterName = name;
	}

}
