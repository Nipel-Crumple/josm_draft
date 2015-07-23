package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;
import java.rmi.server.UID;

import javax.json.Json;
import javax.json.JsonObject;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.rasterfilters.model.FilterStateModel;

import com.jhlabs.image.UnsharpFilter;

public class UnsharpMaskFilter implements Filter {

	private UnsharpFilter unsharp = new UnsharpFilter();
	private FilterStateModel state;
	private float amount;
	private int size;
	private UID id;


	@Override
	public String toString() {
		JsonObject json = Json
				.createObjectBuilder()
				.add("amount",
						Json.createObjectBuilder().add("value", amount).build())
						.add("size",
								Json.createObjectBuilder().add("value", size).build())
								.build();
		return "from unsharp: \n" + json.toString();
	}

	@Override
	public void setState(FilterStateModel newState) {
		this.state = newState;
		changeFilterState(state.encodeJson());
	}

	@Override
	public void setId(UID id) {
		this.id = id;
	}

	public boolean changeFilterState(JsonObject filterState) {
		if (filterState != null) {

			// new value of amount
			JsonObject amountJson = filterState.getJsonObject("amount");
			setAmount((float) amountJson.getJsonNumber("value").doubleValue());

			JsonObject sizeJson = filterState.getJsonObject("size");
			setSize(sizeJson.getJsonNumber("value").intValue());

			Main.debug(id.toString() + " \n" + toString());

			return true;
		}

		return false;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public BufferedImage applyFilter(BufferedImage img) {
		unsharp.setAmount(amount);
		unsharp.setRadius(size);
		return unsharp.filter(img, null);
	}
}
