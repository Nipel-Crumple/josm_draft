package values;

public class SliderValue<Number> implements Value<Number> {

	private String parameterName;
	private Number value;
	
	public SliderValue(Number value, String parameterName) {
		this.value = value;
		this.parameterName = parameterName;
	}
	
	@Override
	public Number getValue() {
		return value;
	}

	@Override
	public void setValue(Number value) {
		this.value = value;
	}

	@Override
	public String getParameterName() {
		return parameterName;
	}
	
	@Override
	public void setParameterName(String name) {
		this.parameterName = name;;
	}
}
