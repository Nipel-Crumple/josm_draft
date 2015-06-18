package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Hashtable;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

class FilterPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public FilterPanel() {
		super();
		setPreferredSize(new Dimension(600, 300));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.black));
		setBackground(Color.white);
	}

	public JComponent addGuiElement(JsonObject json) {
		Border sliderBorder = new EmptyBorder(10, 20, 10, 20);
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
				slider.setMinorTickSpacing(1);
				slider.setMajorTickSpacing(maxValue / 4);
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


			if (!labelTable.isEmpty()) {
				slider.setLabelTable(labelTable);
			}
			
			slider.setBackground(this.getBackground());
			slider.setBorder(sliderBorder);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			Font font = new Font("Arial", Font.PLAIN, 12);
			slider.setFont(font);
			slider.setAlignmentX(Component.LEFT_ALIGNMENT);
			slider.setVisible(true);
			
			this.add(slider);
			return slider;
		} else if (type.equals("checkbox")) {
//			return addFilterLabel(json.getString("title"));
			return null;
		}
		return null;
	}

	public JCheckBox addFilterLabel(String labelText) {
		Border labelBorder = new EmptyBorder(15, 20, 0, 0);
		Font labelFont = new Font("Arial", Font.PLAIN, 14);

		JCheckBox checkLabel = new JCheckBox(labelText);
		checkLabel.setFont(labelFont);
		checkLabel.setFocusable(false);
		checkLabel.setBackground(this.getBackground());
		checkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		checkLabel.setBorder(labelBorder);

		return checkLabel;
	}

	public void addSliderTitle(String labelText) {
		Border labelBorder = new EmptyBorder(15, 20, 0, 0);
		Font labelFont = new Font("Arial", Font.PLAIN, 14);

		JLabel sliderLabel = new JLabel(labelText);
		sliderLabel.setFont(labelFont);
		sliderLabel.setFocusable(false);
		sliderLabel.setBackground(this.getBackground());
		sliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderLabel.setBorder(labelBorder);
		sliderLabel.setVisible(true);

		this.add(sliderLabel);
	}	
}