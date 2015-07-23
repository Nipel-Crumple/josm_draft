package org.openstreetmap.josm.plugins.rasterfilters.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.openstreetmap.josm.plugins.rasterfilters.io.FilterReader;

public class FilterInitializer {

	public static Map<String, JsonObject> filtersWithMeta = new HashMap<>();
	public static Set<String> filterTitles = new TreeSet<>();
	public static Set<URL> urls = new HashSet<>();
	public static List<JsonObject> filtersMeta;
	public static ClassLoader loader;

	public static void initFilters() throws MalformedURLException {

		String dir = "plugins/rasterfilters/meta-inf";

		// reading metainf from file
		FilterReader fr = new FilterReader();

		filtersMeta = fr.readMetaInf(dir);

		for (JsonObject json : filtersMeta) {
			filterTitles.add(json.getString("title"));
			filtersWithMeta.put(json.getString("name"), json);

			JsonArray binaries = json.getJsonArray("binaries");
			for (int i = 0; i < binaries.size(); i++) {
				File file = new File(binaries.getString(i));
				if (file.exists()) {
					URL url = new URL("jar", "", file.toURI().toURL() + "!/");
					urls.add(url);
				}
			}
		}
		loader = new URLClassLoader(urls.toArray(new URL[urls.size()]),
				FilterInitializer.class.getClassLoader());
	}
}
