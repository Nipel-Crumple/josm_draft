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
	private static FilterReader fr;

	public static void initFilters() {

		String dir = "plugins/rasterfilters/meta-inf";

		// reading metainf from file
		fr = new FilterReader();

		filtersMeta = fr.readMetaInf(dir);

		for (JsonObject json : filtersMeta) {

			filterTitles.add(json.getString("title"));
			filtersWithMeta.put(json.getString("name"), json);

			JsonArray binaries = json.getJsonArray("binaries");

			for (int i = 0; i < binaries.size(); i++) {

				File file = new File(binaries.getString(i));

				if (file.exists()) {
					URL url;
					try {
						url = new URL("jar", "", file.toURI().toURL() + "!/");
						urls.add(url);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

		loader = new URLClassLoader(urls.toArray(new URL[urls.size()]),
				FilterInitializer.class.getClassLoader());
	}

	public static void destroyFilters() {
		filtersWithMeta.clear();
		filterTitles.clear();
		urls.clear();
		filtersMeta.clear();
	}
}
