package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.ImageProcessor;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.rasterfilters.model.FilterInitializer;
import org.openstreetmap.josm.plugins.rasterfilters.model.FiltersManager;

public class FiltersDialog {

	public JComboBox<String> filterChooser;
	public JFrame frame;
	public JPanel pane;
	public JButton addButton;
	public DefaultComboBoxModel<String> listModel;
	public JPanel filterContainer;
	public JScrollPane filterContainerScroll;

	public JPanel createFilterContainer() {
		if (filterContainer == null) {

			filterContainer = new JPanel();
			filterContainer.setLayout(new BoxLayout(filterContainer,
					BoxLayout.Y_AXIS));
			filterContainer.setBackground(Color.white);
			// filterHolder.add(Box.createRigidArea(new Dimension(0,10)));
			filterContainerScroll = new JScrollPane(filterContainer,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			pane.add(filterContainerScroll);
		}

		return filterContainer;
	}

	public void deleteFilterContainer() {
		
		Component parent = filterContainerScroll.getParent();
		filterContainerScroll.removeAll();
		((JPanel) parent).remove(filterContainerScroll);

		filterContainer = null;
		
		parent.revalidate();
		parent.repaint();
	}

	public JFrame createAndShowGUI() throws MalformedURLException {
		if (frame != null) {
			frame.setVisible(true);
			return frame;
		} else {

			// filter reading and adding to the collections of
			// FilterInitializer
			FilterInitializer.initFilters();

			frame = new JFrame("Filters");
			frame.setMinimumSize(new Dimension(320, 400));
			frame.setPreferredSize(new Dimension(320, 400));

			pane = new JPanel();
			pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

			pane.setBorder(new EmptyBorder(10, 5, 10, 5));
			pane.setPreferredSize(new Dimension(280, 400));
			pane.setBackground(Color.blue);

			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
			labelPanel.setMaximumSize(new Dimension(300, 20));
			labelPanel.setPreferredSize(new Dimension(300, 20));
			labelPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
			labelPanel.setBackground(Color.white);
			labelPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

			JLabel label = new JLabel("Add filter");
			labelPanel.add(label);
			pane.add(labelPanel);

			// panel that contains the checkBox and add button
			JPanel chooseFilterPanel = new JPanel();
			chooseFilterPanel.setMaximumSize(new Dimension(300, 30));
			chooseFilterPanel.setMinimumSize(new Dimension(300, 30));
			chooseFilterPanel.setLayout(new BoxLayout(chooseFilterPanel,
					BoxLayout.X_AXIS));
			chooseFilterPanel.setBackground(Color.white);

			Set<String> filterTitles = FilterInitializer.filterTitles;

			listModel = new DefaultComboBoxModel<>();
			for (String temp : filterTitles) {
				listModel.addElement(temp);
			}

			filterChooser = new JComboBox<>(listModel);
			filterChooser.setMaximumSize(new Dimension(200, 30));
			chooseFilterPanel.add(filterChooser);

			// empty space area
			chooseFilterPanel.add(Box.createRigidArea(new Dimension(10, 0)));

			if (listModel.getSize() == 0) {
				Main.debug("No metaINF");
			}

			addButton = new JButton();
			addButton.setText("add");
			addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			addButton.setMaximumSize(new Dimension(90, 30));
			addButton.addActionListener(new AddFilterToPanelListener());

			chooseFilterPanel.add(addButton);

			pane.add(chooseFilterPanel);
			pane.add(Box.createRigidArea(new Dimension(300, 20)));

			frame.setContentPane(pane);
			frame.pack();
			frame.setVisible(true);

			return frame;
		}
	}

	public FiltersManager createFilterManager() {
		return new FiltersManager(this);
	}

	class AddFilterToPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			String title = (String) listModel.getSelectedItem();
			JPanel panel = null;
			Layer activeLayer = Main.map.mapView.getActiveLayer();

			if (activeLayer instanceof ImageryLayer) {

				ImageryLayer layer = (ImageryLayer) activeLayer;

				if (layer.getImageProcessors().size() > 0) {

					for (ImageProcessor temp : layer.getImageProcessors()) {
						if (temp instanceof FiltersManager) {
							panel = ((FiltersManager) temp)
									.createPanelByTitle(title);
							break;
						}

					}

				} else {
					FiltersManager fm = createFilterManager();
					layer.addImageProcessor(fm);
					panel = fm.createPanelByTitle(title);
				}
			}

			if (panel != null) {
				filterContainer = createFilterContainer();
				filterContainer.add(panel);
			}

			pane.revalidate();

			listModel.removeElement(title);

			if (listModel.getSize() == 0) {
				addButton.setEnabled(false);
			}

		}
	}

}
