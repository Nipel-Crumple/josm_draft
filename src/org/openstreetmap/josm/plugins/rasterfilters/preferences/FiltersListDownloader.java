package org.openstreetmap.josm.plugins.rasterfilters.preferences;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openstreetmap.josm.Main;

public class FiltersListDownloader {

	private List<FilterInfo> filtersInfo = new ArrayList<>();

	public List<FilterInfo> downloadFilters() {

		JsonObject jsonRequest = Json
				.createObjectBuilder()
				.add("id", new Random().nextInt())
				.add("method", "wiki.getPageHTML")
				.add("params",
						Json.createArrayBuilder().add("ImageFilters").build())
				.build();

		String jsonRequestString = jsonRequest.toString();

		URL wikiApi;
		HttpURLConnection wikiConnection;
		try {
			wikiApi = new URL("https://josm.openstreetmap.de/jsonrpc");
			wikiConnection = (HttpURLConnection) wikiApi.openConnection();
			wikiConnection.setDoOutput(true);
			wikiConnection.setDoInput(true);

			wikiConnection.setRequestProperty("Content-Type",
					"application/json");
			wikiConnection.setRequestProperty("Method", "POST");
			wikiConnection.connect();

			OutputStream os = wikiConnection.getOutputStream();
			os.write(jsonRequestString.getBytes("UTF-8"));
			os.close();

			int HttpResult = wikiConnection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {

				JsonReader jsonStream = Json
						.createReader(new InputStreamReader(wikiConnection
								.getInputStream(), "utf-8"));

				JsonObject jsonResponse = jsonStream.readObject();
				jsonStream.close();

				Elements trTagElems = Jsoup.parse(
						jsonResponse.getString("result"))
						.getElementsByTag("tr");
				for (Element element : trTagElems) {

					Elements elems = element.getElementsByTag("td");
					if (!elems.isEmpty()) {
						String name = elems.get(0).getElementsByTag("a")
								.first().ownText();
						String description = elems.get(1).ownText();
						// TODO Main.pref
						boolean isLoaded = false;

						String link = elems.get(0).getElementsByTag("a").attr("href");

						JsonObject meta = loadMeta(link);
						filtersInfo.add(new FilterInfo(name, description, meta, isLoaded));
					}
				}
				Main.debug(filtersInfo.toString());
			} else {
				Main.debug("Error happenned while requesting for the list of filters");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return filtersInfo;
	}

	public List<FilterInfo> getFiltersInfoList() {
		return filtersInfo;
	}

	public JsonObject loadMeta(String link) {

		Pattern p = Pattern.compile("ImageFilters/\\w.*");
		Matcher m = p.matcher(link);

		if (m.find()) {
			link = link.substring(m.start());
			Main.debug(link);
		}

		JsonObject jsonRequest = Json.createObjectBuilder()
				.add("id", new Random().nextInt())
				.add("method", "wiki.getPageHTML")
				.add("params", Json.createArrayBuilder().add(link).build())
				.build();

		String jsonStringRequest = jsonRequest.toString();

		Main.debug(jsonStringRequest);

		URL wikiApi;
		HttpURLConnection wikiConnection;
		JsonObject meta = null;

		try {
			wikiApi = new URL("https://josm.openstreetmap.de/jsonrpc");
			wikiConnection = (HttpURLConnection) wikiApi.openConnection();
			wikiConnection.setDoOutput(true);
			wikiConnection.setDoInput(true);

			wikiConnection.setRequestProperty("Content-Type",
					"application/json");
			wikiConnection.setRequestProperty("Method", "POST");
			wikiConnection.connect();

			OutputStream os = wikiConnection.getOutputStream();
			os.write(jsonStringRequest.getBytes("UTF-8"));
			os.close();

			int HttpResult = wikiConnection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {

				JsonReader jsonStream = Json
						.createReader(new InputStreamReader(wikiConnection
								.getInputStream(), "UTF-8"));

				JsonObject jsonResponse = jsonStream.readObject();
				jsonStream.close();

				Main.debug(jsonResponse.toString());
				Main.debug(jsonResponse.getString("result"));

				String jsonPage = jsonResponse.getString("result");

				Document doc = Jsoup.parse(jsonPage, "UTF-8");
				String json = doc.getElementsByTag("pre").first().text();

				JsonReader reader = Json.createReader(new StringReader(json));
				meta = reader.readObject();
				reader.close();

			} else {
				System.out.println(wikiConnection.getResponseCode());
				System.out.println(wikiConnection.getResponseMessage());
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return meta;
	}
}