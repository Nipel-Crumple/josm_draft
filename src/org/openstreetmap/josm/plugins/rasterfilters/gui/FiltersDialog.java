package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.ImageProcessor;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.rasterfilters.model.FilterInitializer;
import org.openstreetmap.josm.plugins.rasterfilters.model.FiltersManager;

public class FiltersDialog implements ListSelectionListener, ActionListener {
	
	public JList<String> filtersList;
	public JFrame frame;
	public JPanel pane;
	public JButton addButton;
	public DefaultListModel<String> listModel;
	public JPanel filterContainer;
	
	class AddFilterToPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] indices = filtersList.getSelectedIndices();
			for (int i = indices.length - 1; i >= 0; i--) {
		        String title = listModel.get(indices[i]);
				JPanel panel = null;
				Layer activeLayer = Main.map.mapView.getActiveLayer();
				if (activeLayer instanceof ImageryLayer) {
					ImageryLayer layer = (ImageryLayer) activeLayer;
//					Main.debug("Inside instanceof ImageryLayer; ImageProcessor number is " + layer.getImageProcessors().size());
					if (layer.getImageProcessors().size() > 0) {
    					for (ImageProcessor temp : layer.getImageProcessors()) {
    						if (temp instanceof FiltersManager) {
//    		                    Main.debug("ImageryLayer has FilterManager");
    							panel = ((FiltersManager) temp).createPanelByTitle(title);
    							break;
    						}
    					}
					} else {
//                        Main.debug("ImageryLayer has no FilterManager");
                        FiltersManager fm = createFilterManager();
                        layer.addImageProcessor(fm);
                        panel = fm.createPanelByTitle(title);
                    }
				}
				
				if (panel != null) {
				    filterContainer = createFilterContainer();
	                filterContainer.add(panel);
				}
				
				pane.validate();
				
				listModel.remove(indices[i]);
				if (listModel.getSize() == 0) {
					addButton.setEnabled(false);
				}
			}
			
		}
	}
	
	public JPanel createFilterContainer() {
        if (filterContainer == null) {
            filterContainer = new JPanel();
            filterContainer.setLayout(new BoxLayout(filterContainer, BoxLayout.Y_AXIS));
            filterContainer.setBackground(Color.white);
//          filterHolder.add(Box.createRigidArea(new Dimension(0,10)));
            JScrollPane scrollPanel = new JScrollPane(filterContainer, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pane.add(scrollPanel);
        }
        return filterContainer;
	}

	public JFrame createAndShowGUI() throws MalformedURLException {
		if (frame != null) {
			frame.setVisible(true);
			return frame;
		} else {
			frame = new JFrame("Filters");
			FilterInitializer.initFilters();
			
			pane = new JPanel();
			pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
			
			pane.setBorder(new EmptyBorder(5, 5, 5, 5));
			pane.setPreferredSize(new Dimension(800, 600));
	
			JPanel listPanel = new JPanel();
			listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
			listPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			List<String> filterTitles = new ArrayList<String>(FilterInitializer.filterTitles);
			listModel = new DefaultListModel<>();
			for (String temp : filterTitles) {
				listModel.addElement(temp);
			}
			
			filtersList = new JList<String>(listModel); 
			if (listModel.getSize() == 0) {
				Main.debug("No metaINF");
			}
			filtersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			filtersList.setLayoutOrientation(JList.VERTICAL_WRAP);
			filtersList.setVisibleRowCount(-1);
			JScrollPane listScroller = new JScrollPane(filtersList);
			listScroller.setPreferredSize(new Dimension(320, 100));
			listScroller.setMaximumSize(new Dimension(320, 100));
			listPanel.setPreferredSize(new Dimension(400, 100));
			listPanel.setMaximumSize(new Dimension(400, 100));
			listPanel.add(listScroller);
			
			addButton = new JButton();
			addButton.setText("ADD");
			addButton.setMaximumSize(new Dimension(80, 100));
			addButton.addActionListener(new AddFilterToPanelListener());
			
			
			
			listPanel.add(addButton);
			
			pane.add(listPanel);
			pane.add(Box.createRigidArea(new Dimension(0,10)));
			
			frame.setContentPane(pane);
			frame.pack();
			frame.setVisible(true);
			
			return frame;
		}
	}

	public FiltersManager createFilterManager() {
		return new FiltersManager(this);
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		if (e.getValueIsAdjusting() == false) {
			 
            if (filtersList.getSelectedIndex() == -1) {
            //No selection, disable fire button.
                addButton.setEnabled(false);
 
            } else {
            //Selection, enable the fire button.
                addButton.setEnabled(true);
            }
        }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		FilterPanel filterPanel = (FilterPanel) ((JButton)e.getSource()).getParent();
		listModel.addElement(filterPanel.getName());
		Main.debug(String.valueOf(filterContainer.getComponentCount()));
		filterPanel.removeAll();
		filterContainer.remove(filterPanel);
		filterContainer.revalidate();
		filterContainer.repaint();
		if (!addButton.isEnabled()) {
			addButton.setEnabled(true);
		}
	}
}













