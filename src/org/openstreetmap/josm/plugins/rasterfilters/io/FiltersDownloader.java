package org.openstreetmap.josm.plugins.rasterfilters.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class FiltersDownloader {

	public List<JsonObject> loadMetaInf(String dir) {

		// reading filters meta-INFO from file
		File directory = new File(dir);

		File[] files = findAllMeta(directory);

		List<JsonObject> filtersMeta = new ArrayList<>();

		if (files == null) {
			return filtersMeta;
		}

		for (File temp : files) {
			StringBuilder stringBuilder = new StringBuilder();
			String json = null;
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(temp);
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

			filtersMeta.add(obj);
		}

		return filtersMeta;
	}

	public File[] findAllMeta(File directory) {

		if (directory.isDirectory()) {

			FileFilter filter = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					String regex = "\\w*\\.rf";
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(pathname.getName());
					return m.matches();
				}

			};

			return directory.listFiles(filter);
		} else {
			return null;
		}
	}
}
