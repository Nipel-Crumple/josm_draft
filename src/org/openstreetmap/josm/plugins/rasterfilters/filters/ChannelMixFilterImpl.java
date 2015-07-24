package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;
import java.rmi.server.UID;

import javax.json.Json;
import javax.json.JsonObject;

import org.openstreetmap.josm.Main;

import com.jhlabs.image.ChannelMixFilter;

public class ChannelMixFilterImpl implements Filter {

	private ChannelMixFilter mix = new ChannelMixFilter();
	private int red, green, blue;
	private UID id;

	@Override
	public BufferedImage applyFilter(BufferedImage img) {
		mix.setIntoR(red);
		mix.setIntoG(green);
		mix.setIntoB(blue);
		return mix.filter(img, null);
	}

	@Override
	public String toString() {
		JsonObject json = Json
				.createObjectBuilder()
				.add("red",
						Json.createObjectBuilder()
						.add("value", red)
						.build())
						.add("green", Json.createObjectBuilder()
								.add("value", green)
								.build())
								.add("blue",Json.createObjectBuilder()
										.add("value", blue)
										.build())
										.build();
		return "from channelMix: \n" + json.toString();
	}

	@Override
	public JsonObject changeFilterState(JsonObject filterState) {

		if (filterState != null) {

			// new value of rgb params
			JsonObject redJson = filterState.getJsonObject("red");
			setRed(redJson.getJsonNumber("value").intValue());

			JsonObject greenJson = filterState.getJsonObject("green");
			setGreen(greenJson.getJsonNumber("value").intValue());

			JsonObject blueJson = filterState.getJsonObject("blue");
			setBlue(blueJson.getJsonNumber("value").intValue());

			Main.debug(id.toString() + " \n" + toString());

			return filterState;
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

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public void setRed(int red) {
		this.red = red;
	}

}
