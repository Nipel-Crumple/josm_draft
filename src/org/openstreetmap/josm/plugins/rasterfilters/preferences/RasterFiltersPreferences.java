package org.openstreetmap.josm.plugins.rasterfilters.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.preferences.PreferenceTabbedPane;
import org.openstreetmap.josm.gui.preferences.SubPreferenceSetting;
import org.openstreetmap.josm.gui.preferences.TabPreferenceSetting;
import org.openstreetmap.josm.gui.preferences.map.MapPreference;
import org.openstreetmap.josm.tools.GBC;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

public class RasterFiltersPreferences implements SubPreferenceSetting {

	@Override
	public void addGui(PreferenceTabbedPane gui) {
		JPanel holder = new JPanel();
		holder.setLayout(new GridBagLayout());

		holder.setBorder(new EmptyBorder(10, 10, 10, 10));

		AbstractTableModel model = new FiltersTableModel();
		JTable table = new JTable(model);
		JScrollPane pane = new JScrollPane(table);

		holder.add(pane, GBC.eol().fill(GBC.BOTH));

		GridBagConstraints c = GBC.eol();
		c.anchor = GBC.EAST;
		holder.add(new JButton("Download"), c);

		MapPreference pref = gui.getMapPreference();
		pref.addSubTab(this, "Image Filters", holder);
	}

	@Override
	public boolean ok() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExpert() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TabPreferenceSetting getTabPreferenceSetting(PreferenceTabbedPane gui) {
		return gui.getMapPreference();
	}

	class FiltersTableModel extends AbstractTableModel {

		String[] columnNames = { "Filter Name", "Description", "Downloaded" };
		Class[] columnClasses = { String.class, String.class, Boolean.class };
		List<FilterInfo> filtersList;
		Object[][] data;

		public FiltersTableModel() {

			filtersList = FiltersListDownloader.downloadFilters();
			data = new Object[filtersList.size()][2];

			for (int i = 0; i < filtersList.size(); i++) {
				data[i][0] = filtersList.get(i).getName();
				data[i][1] = filtersList.get(i).getDescription();
			}

		}

		@Override
		public int getRowCount() {
			return filtersList.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
				case 0: return data[rowIndex][0];
				case 1: return data[rowIndex][1];
				case 2: return filtersList.get(rowIndex).isDownloaded();
				default: return null;
			}
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Class getColumnClass(int col) {
			return columnClasses[col];
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col == 2) {
				return true;
			}

			return false;
		}
	}

}

class FilterInfo {
	private String name;
	private String description;
	private Boolean isDownloaded;

	public FilterInfo() {

	}

	public FilterInfo(String name, String description, boolean isDownloaded) {
		this.setName(name);
		this.setDescription(description);
		this.setDownloaded(isDownloaded);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isDownloaded() {
		return isDownloaded;
	}

	public void setDownloaded(boolean isDownloaded) {
		this.isDownloaded = isDownloaded;
	}

}

class FiltersListDownloader {

	private static List<FilterInfo> filtersInfo = new ArrayList<>();

	public static List<FilterInfo> downloadFilters() {

		List<Object> positionalParams = new ArrayList<Object>();

		JSONRPC2Request reqOut = new JSONRPC2Request("wiki.getPageHTML",
				new Random().nextInt());

		// name of the needed wiki page
		positionalParams.add("ImageFilters");
		reqOut.setPositionalParams(positionalParams);

		String jsonString = reqOut.toString();

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
			os.write(jsonString.getBytes("UTF-8"));
			os.close();

			int HttpResult = wikiConnection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {

				JsonReader jsonStream = Json
						.createReader(new InputStreamReader(wikiConnection
								.getInputStream(), "utf-8"));

				JsonObject jsonResponse = jsonStream.readObject();
				jsonStream.close();

				Elements trTagElems =  Jsoup.parse(jsonResponse.getString("result"))
						.getElementsByTag("tr");
				for (Element element : trTagElems) {

					Elements elems = element.getElementsByTag("td");
					if (!elems.isEmpty()) {
						filtersInfo.add(new FilterInfo(elems.get(0).ownText(),
								elems.get(1).ownText(), false));
					}
				}

			} else {
				Main.debug("Error happenned while requesting for the list of filters");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return filtersInfo;
	}

	public static List<FilterInfo> getFiltersInfoList() {
		return filtersInfo;
	}
}


