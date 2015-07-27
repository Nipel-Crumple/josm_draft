package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.rasterfilters.model.FilterInitializer;
import org.openstreetmap.josm.plugins.rasterfilters.model.FiltersManager;

public class FiltersDialog {

	public JFrame frame;
	private JComboBox<String> filterChooser;
	private JPanel pane;
	private JButton addButton;
	private DefaultComboBoxModel<String> listModel;
	private JPanel filterContainer;
	private Layer layer;
	private FiltersManager filtersManager;
	private JScrollPane filterContainerScroll;

	public FiltersDialog(ImageryLayer layer) {
		this.setLayer(layer);
		this.setFiltersManager(new FiltersManager(this));
		layer.addImageProcessor(getFiltersManager());
	}

	public JPanel createFilterContainer() {
		if (getFilterContainer() == null) {

			setFilterContainer(new JPanel());
			getFilterContainer().setLayout(new BoxLayout(getFilterContainer(),
					BoxLayout.Y_AXIS));
			getFilterContainer().setBackground(Color.white);

			filterContainerScroll = new JScrollPane(getFilterContainer(),
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			pane.add(filterContainerScroll);

		}

		return getFilterContainer();
	}

	public void deleteFilterContainer() {

		Component parent = filterContainerScroll.getParent();
		filterContainerScroll.removeAll();
		((JPanel) parent).remove(filterContainerScroll);

		setFilterContainer(null);

		parent.revalidate();
		parent.repaint();
	}

	public JFrame createAndShowGUI() throws MalformedURLException {
		if (frame != null) {
			frame.setVisible(true);
			return frame;
		} else {

			frame = new JFrame();
			String title = "Filters | " + layer.getName();
			frame.setTitle(title);
			frame.setMinimumSize(new Dimension(350, 420));
			frame.setPreferredSize(new Dimension(350, 420));

			pane = new JPanel();
			pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

			pane.setBorder(new EmptyBorder(10, 5, 10, 5));
			pane.setPreferredSize(new Dimension(300, 400));
			pane.setBackground(Color.white);

			JPanel topPanel = new JPanel();
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
			topPanel.setMaximumSize(new Dimension(300, 50));
			topPanel.setMinimumSize(new Dimension(300, 50));
			topPanel.setBackground(Color.white);

			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
			labelPanel.setMaximumSize(new Dimension(300, 20));
			labelPanel.setBackground(Color.white);

			JLabel label = new JLabel("Add filter");
			labelPanel.add(label);
//			pane.add(labelPanel);

			// TODO why after add clicked the top panel is resized???

			// panel that contains the checkBox and add button
			JPanel chooseFilterPanel = new JPanel();
			chooseFilterPanel.setMinimumSize(new Dimension(300, 30));
			chooseFilterPanel.setLayout(new BoxLayout(chooseFilterPanel,
					BoxLayout.X_AXIS));
			chooseFilterPanel.setBackground(Color.white);

			Set<String> filterTitles = FilterInitializer.filterTitles;

			setListModel(new DefaultComboBoxModel<String>());
			for (String temp : filterTitles) {
				getListModel().addElement(temp);
			}

			filterChooser = new JComboBox<>(getListModel());
			filterChooser.setMaximumSize(new Dimension(200, 30));
			chooseFilterPanel.add(filterChooser);

			// empty space area between select and add button
			chooseFilterPanel.add(Box.createRigidArea(new Dimension(10, 0)));

			if (getListModel().getSize() == 0) {
				Main.debug("No metaINF");
			}

			setAddButton(new JButton());
			getAddButton().setText("add");
			getAddButton().setAlignmentX(Component.CENTER_ALIGNMENT);
			getAddButton().setMaximumSize(new Dimension(90, 30));
			getAddButton().addActionListener(new AddFilterToPanelListener());

			// check if there is no meta information
			if (FilterInitializer.filtersMeta.isEmpty()) {
				getAddButton().setEnabled(false);
				filterChooser.setEnabled(false);
			} else {
				getAddButton().setEnabled(true);
				filterChooser.setEnabled(true);
			}

			chooseFilterPanel.add(getAddButton());

			topPanel.add(labelPanel);
			topPanel.add(chooseFilterPanel);
			pane.add(topPanel);
//			pane.add(chooseFilterPanel);
//			pane.add(Box.createRigidArea(new Dimension(0, 20)));

			frame.setContentPane(pane);
			frame.pack();
			frame.setVisible(true);

			return frame;
		}
	}

	public FiltersManager createFilterManager() {
		return new FiltersManager(this);
	}

	public void closeFrame() {
		frame.dispose();
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public JPanel getFilterContainer() {
		return filterContainer;
	}

	public void setFilterContainer(JPanel filterContainer) {
		this.filterContainer = filterContainer;
	}

	public DefaultComboBoxModel<String> getListModel() {
		return listModel;
	}

	public void setListModel(DefaultComboBoxModel<String> listModel) {
		this.listModel = listModel;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public void setAddButton(JButton addButton) {
		this.addButton = addButton;
	}

	public FiltersManager getFiltersManager() {
		return filtersManager;
	}

	public void setFiltersManager(FiltersManager filtersManager) {
		this.filtersManager = filtersManager;
	}

	class AddFilterToPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			String title = (String) getListModel().getSelectedItem();
			JPanel panel = null;

			panel = getFiltersManager().createPanelByTitle(title);

			if (panel != null) {
				setFilterContainer(createFilterContainer());
				getFilterContainer().add(panel);
			}

			getListModel().removeElement(title);

			if (getListModel().getSize() == 0) {
				getAddButton().setEnabled(false);
			}

		}
	}
}
