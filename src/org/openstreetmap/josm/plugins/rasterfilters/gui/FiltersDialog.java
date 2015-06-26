package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class FiltersDialog {

	public JFrame createAndShowGUI() {
		JFrame frame = new JFrame("Filters");
		
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		FiltersManager fm = new FiltersManager();

		List<JPanel> panels = fm.createFilterPanels();
		
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setPreferredSize(new Dimension(800, 600));

        // set the maximum width to the current screen. If the dialog is opened on a
        // smaller screen than before, this will reset the stored preference.
		// topPanel.setMaximumSize( Toolkit.getDefaultToolkit().getScreenSize());
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Object[] data = fm.getFilterTitles().toArray();
		JList list = new JList(data); 
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(320, 100));
		listScroller.setMaximumSize(new Dimension(320, 100));
		listScroller.setMinimumSize(new Dimension(100, 100));
		listPanel.setPreferredSize(new Dimension(400, 100));
		listPanel.setMaximumSize(new Dimension(400, 100));
		listPanel.setMinimumSize(new Dimension(100, 100));
		listPanel.add(listScroller);
		listPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		
		JButton addButton = new JButton();
		addButton.setText("ADD");
		addButton.setMaximumSize(new Dimension(80, 100));
		
		listPanel.add(addButton);
		
		pane.add(listPanel);
		pane.add(Box.createRigidArea(new Dimension(0,10)));
		
		JPanel filterHolder = new JPanel();
		filterHolder.setLayout(new BoxLayout(filterHolder, BoxLayout.Y_AXIS));
		filterHolder.setMaximumSize(new Dimension(100, 300));
		filterHolder.add(Box.createRigidArea(new Dimension(0,10)));
		
		for (JPanel panel : panels) {
			filterHolder.add(panel);
			filterHolder.add(Box.createRigidArea(new Dimension(0,10)));
		}
		JScrollPane scrollPanel = new JScrollPane(filterHolder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
//		pane.add(filterHolder);
		pane.add(scrollPanel);
		frame.setContentPane(pane);
		frame.pack();
		frame.setVisible(true);
		
		return frame;
	}
}













