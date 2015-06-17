package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class FilterReader {

	public JsonObject readMetaInf() {
		// reading filters meta-INFO from file
		File file = new File("sharpen.txt");
		StringBuilder stringBuilder = new StringBuilder();
		String json = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
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
			e.printStackTrace();
		}
		// json string with META-INFO
		json = stringBuilder.toString();

		// Creating reader for parsing json META-INFO string
		JsonReader reader = Json.createReader(new StringReader(json));

		JsonObject obj = reader.readObject();

		reader.close();
		
		return obj;
	}
}
