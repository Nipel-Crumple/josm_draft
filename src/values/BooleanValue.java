package values;

public class BooleanValue implements Value<Boolean> {

	private Boolean value;
	private String parameterName;

	public BooleanValue(Boolean value, String parameterName) {
		this.value = value;
		this.parameterName = parameterName;
	}
	
	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void setValue(Boolean value) {
		this.value = value;
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
