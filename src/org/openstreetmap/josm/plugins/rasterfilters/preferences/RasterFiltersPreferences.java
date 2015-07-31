package org.openstreetmap.josm.plugins.rasterfilters.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.json.JsonObject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.openstreetmap.josm.gui.preferences.PreferenceTabbedPane;
import org.openstreetmap.josm.gui.preferences.SubPreferenceSetting;
import org.openstreetmap.josm.gui.preferences.TabPreferenceSetting;
import org.openstreetmap.josm.gui.preferences.map.MapPreference;
import org.openstreetmap.josm.tools.GBC;

public class RasterFiltersPreferences implements SubPreferenceSetting {

	private FiltersListDownloader downloader = new FiltersListDownloader();
	@Override
	public void addGui(PreferenceTabbedPane gui) {
		JPanel holder = new JPanel();
		holder.setLayout(new GridBagLayout());

		holder.setBorder(new EmptyBorder(10, 10, 10, 10));

		AbstractTableModel model = new FiltersTableModel();
		model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int col = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				String columnName = model.getColumnName(col);

				if (columnName.equals("Downloaded")) {

					Boolean isDownloadedUpdate = (Boolean) model.getValueAt(row, col);
					List<FilterInfo> filtersList = ((FiltersTableModel) model).filtersList;

					filtersList.get(row).setDownloaded(isDownloadedUpdate);
				}

			}
		});

		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		JScrollPane pane = new JScrollPane(table);

		holder.add(pane, GBC.eol().fill(GBC.BOTH));

		GridBagConstraints c = GBC.eol();
		c.anchor = GBC.EAST;

		JButton download = new JButton("Download");
		holder.add(download, c);

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

			filtersList = downloader.downloadFilters();
			data = new Object[filtersList.size()][3];

			for (int i = 0; i < filtersList.size(); i++) {
				data[i][0] = filtersList.get(i).getName();
				data[i][1] = filtersList.get(i).getDescription();
				data[i][2] = filtersList.get(i).isDownloaded();
			}

		}

		@Override
		public int getRowCount() {
			return filtersList.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
				case 0: return data[rowIndex][0];
				case 1: return data[rowIndex][1];
				case 2: return data[rowIndex][2];
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

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

}

class FilterInfo {
	private String name;
	private String description;
	private JsonObject meta;
	private Boolean isDownloaded;

	public FilterInfo() {

	}

	public FilterInfo(String name, String description, JsonObject meta, boolean isDownloaded) {
		this.setName(name);
		this.setDescription(description);
		this.meta = meta;
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

	public JsonObject getMeta() {
		return meta;
	}

	public void setMeta(JsonObject meta) {
		this.meta = meta;
	}

	public Boolean isDownloaded() {
		return isDownloaded;
	}

	public void setDownloaded(boolean isDownloaded) {
		this.isDownloaded = isDownloaded;
	}

	@Override
	public String toString() {
		return "name: " + getName() +
				"\nDescription: " + getDescription() +
				"\nDownloaded: " + isDownloaded() +
				"\nMeta: " + getMeta();
	}
}


