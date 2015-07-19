package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.rmi.server.UID;
import java.util.Hashtable;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.openstreetmap.josm.plugins.rasterfilters.model.FiltersManager;

public class FilterPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private UID filterId;

	public FilterPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMaximumSize(new Dimension(300, 300));
		setBorder(BorderFactory.createLineBorder(Color.black));
		setBackground(Color.yellow);
	}

	public JComponent addGuiElement(JsonObject json) {
		Border sliderBorder = new EmptyBorder(5, 5, 5, 5);
		String type = json.getString("type");
		
		if (type.equals("linear_slider")) {
			JSlider slider = null;
			addSliderTitle(json.getString("title"));
			Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
			
			JsonArray array = json.getJsonArray("scale");
			String valueType = json.getString("value_type");
			if (valueType.equals("integer")) {
				int minValue = array.getInt(0);
				int maxValue = array.getInt(1);
				int initValue = json.getInt("default");
				
				slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, initValue);
				slider.setName(json.getString("name"));
				slider.setToolTipText(String.valueOf(slider.getValue()));
				slider.setMinorTickSpacing(maxValue / 4);
//				slider.setMajorTickSpacing(maxValue / 4);
			} else if (valueType.equals("float")) {
				//every value is supplied by 10 to be integer for slider
				int minValue = array.getInt(0) * 100;
				int maxValue = array.getInt(1) * 100;
				double initValue = json.getJsonNumber("default").doubleValue() * 100;
				double delta = (maxValue - minValue) / 100;
				for (int i = 0; i <= maxValue; i++) {
					if ((i % 20) == 0) {
						labelTable.put(new Integer(i), new JLabel(String.valueOf(i * delta / 100)));
					}
				}
				slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, new Double(initValue).intValue());
				slider.setName(json.getString("name"));
				slider.setToolTipText(String.valueOf((double) slider.getValue() / 100));
			}


//			if (!labelTable.isEmpty()) {
//				slider.setLabelTable(labelTable);
//			}
			
			slider.setBackground(this.getBackground());
			slider.setBorder(sliderBorder);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
//			Font font = new Font("Arial", Font.PLAIN, 10);
//			slider.setFont(font);
//			slider.setAlignmentX(Component.LEFT_ALIGNMENT);
			slider.setVisible(true);
			
			this.add(slider);
			return slider;
		} else if (type.equals("checkbox")) {
//			return addFilterLabel(json.getString("title"));
			return null;
		}
		return null;
	}

	public JPanel addFilterLabel(String labelText) {
		JPanel labelPanel = new JPanel();
		labelPanel.setMaximumSize(new Dimension(300, 20));
		labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JLabel filterLabel = new JLabel(labelText);
		Font labelFont = new Font("Arial", Font.PLAIN, 12);
		
		filterLabel.setFont(labelFont);
		
		labelPanel.add(filterLabel);
		
		return labelPanel;
	}
	
	public JCheckBox createDisableBox(ItemListener listener) {
		JCheckBox disable = new JCheckBox("Disable");
		Font font = new Font("Arial", Font.PLAIN, 12);
		
		disable.addItemListener(listener);
		disable.setFont(font);
		
		return disable;
	}
	
	public JButton createRemoveButton(ActionListener listener) {
		JButton removeButton = new JButton("Delete");
		Font font = new Font("Arial", Font.PLAIN, 12);
		
		removeButton.setFont(font);
		removeButton.setName("delete");
		
		removeButton.addActionListener(listener);
		
		return removeButton;
	}
	
	public JPanel createBottomPanel(FiltersManager listener) {
		JPanel bottom = new JPanel();
		
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		
		bottom.add(createDisableBox(listener));
		bottom.add(Box.createHorizontalGlue());
		bottom.add(createRemoveButton(listener));
		
		return bottom;
	}

	private void addSliderTitle(String labelText) {
		Font labelFont = new Font("Arial", Font.PLAIN, 14);
		
		JPanel sliderLabelPanel = new JPanel();
		sliderLabelPanel.setMaximumSize(new Dimension(400, 30));
		sliderLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//		sliderLabelPanel.setAlignmentX(LEFT_ALIGNMENT);
		sliderLabelPanel.setBackground(Color.green);

		JLabel sliderLabel = new JLabel(labelText, JLabel.LEFT);
		sliderLabel.setFont(labelFont);
		sliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderLabel.setVisible(true);

		sliderLabelPanel.add(sliderLabel);
		
		this.add(sliderLabelPanel);
	}	
	
	public void setFilterId(UID filterId) {
		this.filterId = filterId;
	}
	
	public UID getFilterId() {
		return filterId;
	}
}