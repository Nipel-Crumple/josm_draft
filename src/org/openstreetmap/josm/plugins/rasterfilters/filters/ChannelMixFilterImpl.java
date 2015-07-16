package org.openstreetmap.josm.plugins.rasterfilters.filters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.server.UID;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.rasterfilters.model.FilterStateModel;

import com.jhlabs.image.ChannelMixFilter;

public class ChannelMixFilterImpl implements Filter {
	
	private ChannelMixFilter mix = new ChannelMixFilter();
	private FilterStateModel state;
	private int red, green, blue;
	private UID id;
	private boolean enabled;

	@Override
	public boolean changeFilterState(JsonObject filterState) {
		if (filterState != null) {
			
			enabled = filterState.getBoolean("enabled");
			
			// new value of rgb params
			JsonObject redJson = filterState.getJsonObject("red");
			setRed(redJson.getJsonNumber("value").intValue());			

			JsonObject greenJson = filterState.getJsonObject("green");
			setGreen(greenJson.getJsonNumber("value").intValue());

			JsonObject blueJson = filterState.getJsonObject("blue");
			setBlue(blueJson.getJsonNumber("value").intValue());
			
			Main.debug(id.toString() + " \n" + toString());
			
			return true;
		}

		return false;
	}

	@Override
	public BufferedImage applyFilter(BufferedImage img) {
		if (isEnabled()) {
			mix.setIntoR(red);
			mix.setIntoG(green);
			mix.setIntoB(blue);
			return mix.filter(img, null);
		} else {
			return img;
		}
	}

	@Override
	public void setState(FilterStateModel state) {
		this.state = state;
		changeFilterState(state.encodeJson());
	}

	@Override
	public void setId(UID id) {
		this.id = id;
	}
	

	@Override
	public String toString() {
		JsonObject json = Json.createObjectBuilder()
			.add("enabled", enabled)
			.add("red", Json.createObjectBuilder()
					.add("value", red)
					.build())
			.add("green", Json.createObjectBuilder()
					.add("value", green)
					.build())
			.add("blue", Json.createObjectBuilder()
					.add("value", blue)
					.build())
			.build();
		return "from ChannelMix: \n" + json.toString();
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
