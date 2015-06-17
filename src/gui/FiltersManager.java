package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

class FiltersManager implements StateChangeListener {
	
	private Map<String, FilterStateOwner> states = new HashMap();
	
	public JPanel createFilterGUI() {
		
		FilterPanel fp = new FilterPanel();
		// reading filters meta-INFO from file
		File file = new File("sharpen.txt");
		StringBuilder stringBuilder = new StringBuilder();
		String json = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedReader buff = new BufferedReader(fileReader);
		String line;

		try {
			while ((line = buff.readLine()) != null) {
				stringBuilder.append(line);
			}
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// json string with META-INFO
		json = stringBuilder.toString();

		// Creating reader for parsing json META-INFO string
		JsonReader reader = Json.createReader(new StringReader(json));

		JsonObject obj = reader.readObject();
		
		reader.close();
		
		//listener to track sliders and checkbox of creating filter
		FilterGuiListener filterListener = new FilterGuiListener(this);
		
		this.states.put(obj.getString("name"), filterListener);

		JCheckBox checkBox = fp.addFilterLabel(obj.getString("title"));
		checkBox.addItemListener(filterListener);
		fp.add(checkBox);
		
		JsonArray controls = obj.getJsonArray("controls");
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		for (int i = 0; i < controls.size(); i++) {
			JsonObject temp = controls.getJsonObject(i);
			JSlider slider = fp.addSlider(temp);
			slider.addChangeListener(filterListener);
			
			jsonBuilder.add(temp.getString("name"), Json.createObjectBuilder()
					.add("value", temp.getJsonNumber("default")));
			
			fp.add(slider);
		}
		
		JsonObject jsonFilterState = jsonBuilder.build();
		
		filterListener.setFilterState(jsonFilterState);
		
		return fp;
	}

	/**
	 * 
	 * @param json - json that contains info about filter which was changed
	 */
	@Override
	public void filterStateChanged(JsonObject json) {
		// create json msg for sending to all instances of filters
		System.out.println("filter state was changed to " + json.toString());
	}

	
}
