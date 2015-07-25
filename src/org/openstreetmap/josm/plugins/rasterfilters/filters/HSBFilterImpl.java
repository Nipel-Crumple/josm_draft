package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;
import java.rmi.server.UID;

import javax.json.Json;
import javax.json.JsonObject;

import com.jhlabs.image.HSBAdjustFilter;

public class HSBFilterImpl implements Filter {

	private HSBAdjustFilter hsb = new HSBAdjustFilter();
	private float hFactor, sFactor, bFactor;

	private UID id;

	@Override
	public JsonObject changeFilterState(JsonObject filterState) {

		JsonObject hJson = filterState.getJsonObject("hue");
		hFactor = (float) hJson.getJsonNumber("value").doubleValue();

		JsonObject sJson = filterState.getJsonObject("saturation");
		sFactor = (float) sJson.getJsonNumber("value").doubleValue();

		JsonObject bJson = filterState.getJsonObject("brightness");
		bFactor = (float) bJson.getJsonNumber("value").doubleValue();

		return filterState;
	}

	@Override
	public BufferedImage applyFilter(BufferedImage img) {

		hsb.setHFactor(hFactor);
		hsb.setSFactor(sFactor);
		hsb.setBFactor(bFactor);

		img = hsb.filter(img, null);

		return img;
	}

	@Override
	public void setId(UID id) {
		this.id = id;
	}

	@Override
	public UID getId() {
		return id;
	}

	@Override
	public String toString() {
		JsonObject json = Json
				.createObjectBuilder()
				.add("hFactor",
						Json.createObjectBuilder()
								.add("value", hFactor)
								.build())
				.add("sFactor",
						Json.createObjectBuilder()
								.add("value", sFactor)
								.build())
				.add("bFactor",
						Json.createObjectBuilder()
								.add("value", bFactor)
								.build())
				.build();

		return "from contrast: \n" + json.toString();
	}

}
