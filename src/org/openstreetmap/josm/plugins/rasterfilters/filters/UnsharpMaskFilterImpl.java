package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;
import java.rmi.server.UID;

import javax.json.Json;
import javax.json.JsonObject;

import org.openstreetmap.josm.Main;

import com.jhlabs.image.UnsharpFilter;

public class UnsharpMaskFilterImpl implements Filter {

	private UnsharpFilter unsharp = new UnsharpFilter();
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
	public JsonObject changeFilterState(JsonObject newFilterState) {
		if (newFilterState != null) {

			// new value of amount
			JsonObject amountJson = newFilterState.getJsonObject("amount");
			setAmount((float) amountJson.getJsonNumber("value").doubleValue());

			JsonObject sizeJson = newFilterState.getJsonObject("size");
			setSize(sizeJson.getJsonNumber("value").intValue());

			Main.debug(id.toString() + " \n" + toString());

			return newFilterState;
		}

		return null;
	}

	@Override
	public void setId(UID id) {
		this.id = id;
	}

	@Override
	public UID getId() {
		return id;
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
