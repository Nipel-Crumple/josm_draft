package filters;

import javax.json.Json;
import javax.json.JsonObject;

import com.jhlabs.image.UnsharpFilter;

public class UnsharpMaskFilter implements Filter {

	private UnsharpFilter unsharp = new UnsharpFilter();
	private double amount;
	private int size;

	public UnsharpMaskFilter(double amount, int size) {
		this.amount = amount;
		this.size = size;
	}

	public UnsharpMaskFilter() {

	}

	@Override
	public boolean changeFilterState(JsonObject newState) {
		if (newState != null) {
			// new value of amount
			JsonObject amountJson = newState.getJsonObject("amount");
			setAmount(amountJson.getJsonNumber("value").doubleValue());

			JsonObject sizeJson = newState.getJsonObject("size");
			setSize(sizeJson.getJsonNumber("value").intValue());
			
			if (toString().equals(newState.toString())) {
				System.out.println("from unsharp: \n" + toString());
			}
			
			return true;
		}

		return false;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	
	@Override
	public String toString() {
		JsonObject json = Json.createObjectBuilder()
			.add("amount", Json.createObjectBuilder()
					.add("value", amount)
					.build())
			.add("size", Json.createObjectBuilder()
					.add("value", size)
					.build())
			.build();
		return json.toString();
	}
}
