package org.openstreetmap.josm.plugins.rasterfilters.model;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.ImageProcessor;
import org.openstreetmap.josm.plugins.rasterfilters.filters.Filter;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterGuiListener;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterPanel;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FilterStateOwner;
import org.openstreetmap.josm.plugins.rasterfilters.gui.FiltersDialog;

public class FiltersManager implements StateChangeListener, ImageProcessor, ActionListener, ItemListener {
	
	public Map<UID, Filter> filtersMap = new LinkedHashMap<>();
	public Set<Filter> disabledFilters = new HashSet<>();
	public FiltersDialog dialog;
	
	public FiltersManager(FiltersDialog dialog) {
		this.dialog = dialog;
	}
	
	private JPanel createFilterWithPanel(JsonObject meta) {
		
		FilterPanel fp = new FilterPanel();
		
		//listener to track sliders and checkbox of creating filter
		FilterGuiListener filterListener = new FilterGuiListener(this);
		
		String filterClassName = meta.getString("classname");
		
		String filterTitle = meta.getString("title");
		
		fp.setName(filterTitle);
		// creating model of the filter
		FilterStateModel filterState = new FilterStateModel();
		filterState.setFilterClassName(filterClassName);
		
		//loading jar with filter at runtime
		Class<?> clazz;
		
		//filter for adding to map states
		Filter filter = null;
		
		try {
			
			clazz = FilterInitializer.loader.loadClass(filterState.getFilterClassName());
			filter = (Filter) clazz.newInstance();
			
		} catch (InstantiationException | IllegalAccessException e) {
			
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		if (filter != null) {
			
			UID filterId = new UID();
			fp.setFilterId(filterId);
			filterListener.setFilterId(filterId);
			filter.setId(filterId);
			filtersMap.put(filterId, filter);
			
			// all filters enabled in the beggining by default
		}

		fp.setBorder(BorderFactory.createTitledBorder(meta.getString("title")));
		
		JsonArray controls = meta.getJsonArray("controls");
		
		for (int i = 0; i < controls.size(); i++) {
			
			JsonObject temp = controls.getJsonObject(i);
//			Main.debug(temp.toString());

			JComponent component = fp.addGuiElement(temp);
			
			if (component != null) {
				
				if (component instanceof JSlider) {
					((JSlider) component).addChangeListener(filterListener);
				} else if (component instanceof JCheckBox) {
					((JCheckBox) component).addItemListener(filterListener);
				} else if (component instanceof JComboBox) {
					((JComboBox<String>) component).addActionListener(filterListener);
				}
				
				// adding parameters to the filter instance
				filterState.addParams(temp);
			}
			
		}

		fp.setNeededHeight(fp.getNeededHeight() + 60);
		fp.setMaximumSize(new Dimension(300, fp.getNeededHeight()));
		
		filter.setState(filterState);
		
		fp.createBottomPanel(this);
		
		filterListener.setFilterState(filterState);
		
		Main.debug("The number of elems in the Filters map is equal \n" + filtersMap.size());
		
		return fp;
	}

	
	/**
	 * The method notifies about changes in the filter's status
	 * 
	 * @param filterState - model that contains info about filter which was changed
	 */
	@Override
	public void filterStateChanged(UID filterId, FilterStateModel filterState) {
		
		filtersMap.get(filterId).changeFilterState(filterState.encodeJson());
		
		Main.map.mapView.getActiveLayer().setFilterStateChanged();
		
		Main.debug("Current state" + filterState.encodeJson().toString());
		
	}	
	
	public JPanel createPanelByTitle(String title) {
		
		for (JsonObject json : FilterInitializer.filtersMeta) {
			
			if (json.getString("title").equals(title)) {
				return createFilterWithPanel(json);
			}
		}
		
		return null;
	}

	@Override
	public BufferedImage process(BufferedImage image) {
		
		Iterator<Filter> it = filtersMap.values().iterator();
		
		//iterating through map of filters according to the order
		while (it.hasNext()) {
			
			Filter curFilter = it.next();

			if (!disabledFilters.contains(curFilter)) {
				// if next filter will return null
				// we should take an old example of the image
				BufferedImage oldImg = image;
				
				// applying filter to the current image
				image = curFilter.applyFilter(image);
				
				if (image == null) {
					image = oldImg;
				}
			}
		}
		
		return image;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		FilterPanel filterPanel = (FilterPanel) ((JButton) e.getSource())
				.getParent().getParent();
		
		UID filterId = filterPanel.getFilterId();
		
		// removing filter from the filters chain
		filtersMap.remove(filterId);
		
		// add filterTitle to the 'choose list' on the top
		dialog.listModel.addElement(filterPanel.getName());
		
		//removing panel from filterContainer
		filterPanel.removeAll();
		dialog.filterContainer.remove(filterPanel);

		if (dialog.filterContainer.getComponentCount() == 0) {
			
			dialog.deleteFilterContainer();
			
		} else {

			dialog.filterContainer.revalidate();
			dialog.filterContainer.repaint();
			
		}
		
		// if there were no elements in the list 
		// but then it appeared 
		// button should be enabled
		if (!dialog.addButton.isEnabled()) {
			dialog.addButton.setEnabled(true);
		}
		
		Main.map.mapView.getActiveLayer().setFilterStateChanged();
		
	}
	

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		JCheckBox enableFilter = (JCheckBox) e.getSource();
		FilterPanel filterPanel = (FilterPanel) enableFilter.getParent().getParent();
		
		if (enableFilter.isSelected()) {
			
			UID filterId = filterPanel.getFilterId();
			disabledFilters.add(filtersMap.get(filterId));
			
			Main.map.mapView.getActiveLayer().setFilterStateChanged();
			
		} else {
			
			UID filterId = filterPanel.getFilterId();
			disabledFilters.remove(filtersMap.get(filterId));
			
			Main.map.mapView.getActiveLayer().setFilterStateChanged();
		
		}
	}	
}
