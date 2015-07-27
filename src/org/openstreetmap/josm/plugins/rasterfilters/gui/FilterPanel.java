package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.rmi.server.UID;
import java.util.Hashtable;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
	private int neededHeight;

	public FilterPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		// setMaximumSize(new Dimension(300, 400));
		setBackground(Color.white);
	}

	public JComponent addGuiElement(JsonObject json) {
		String type = json.getString("type");

		if (type.equals("linear_slider")) {

			setNeededHeight(getNeededHeight() + 70);

			return createSlider(json);

		} else if (type.equals("checkbox")) {

			setNeededHeight(getNeededHeight() + 30);

			JCheckBox checkBox = createCheckBox(json.getString("title"));
			checkBox.setName(json.getString("name"));

			return checkBox;

		} else if (type.equals("select")) {

			setNeededHeight(getNeededHeight() + 50);

			return createSelect(json);
		}
		return null;
	}

	private JComponent createSelect(JsonObject json) {

		Font font = new Font("Arial", Font.PLAIN, 14);

		JPanel selectPanel = new JPanel();

		selectPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		selectPanel.setBackground(Color.white);
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
		selectPanel.setMaximumSize(new Dimension(300, 40));

		JLabel selectTitle = new JLabel(json.getString("title"));

		selectTitle.setFont(font);
		selectTitle.setBackground(Color.white);

		JsonArray valuesArray = json.getJsonArray("values");

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

		model.setSelectedItem(json.getString("default"));

		for (int i = 0; i < valuesArray.size(); i++) {
			model.addElement(valuesArray.getString(i));
		}

		JComboBox<String> selectBox = new JComboBox<>(model);
		selectBox.setMinimumSize(new Dimension(140, 30));

		selectPanel.add(selectTitle);
		selectPanel.add(Box.createHorizontalGlue());
		selectPanel.add(selectBox);
		selectBox.setName(json.getString("name"));

		this.add(selectPanel);

		return selectBox;
	}

	public JCheckBox createCheckBox(String text) {

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setMaximumSize(new Dimension(300, 30));
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.X_AXIS));
		checkBoxPanel.setBackground(Color.white);

		JCheckBox checkBox = new JCheckBox(text);
		Font font = new Font("Arial", Font.PLAIN, 12);

		checkBox.setFont(font);
		checkBox.setBackground(Color.white);
		checkBox.setName(text);

		checkBoxPanel.add(checkBox);

		this.add(checkBoxPanel);

		return checkBox;
	}

	private JCheckBox createDisableBox(ItemListener listener) {
		JCheckBox disable = new JCheckBox("Disable");
		Font font = new Font("Arial", Font.PLAIN, 12);

		disable.addItemListener(listener);
		disable.setFont(font);

		return disable;
	}

	private JButton createRemoveButton(ActionListener listener) {
		JButton removeButton = new JButton("Delete");
		Font font = new Font("Arial", Font.PLAIN, 12);

		removeButton.setFont(font);
		removeButton.setName("delete");

		removeButton.addActionListener(listener);

		return removeButton;
	}

	public JPanel createBottomPanel(FiltersManager listener) {

		this.add(Box.createRigidArea(new Dimension(0, 10)));
		JPanel bottom = new JPanel();

		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		bottom.setMaximumSize(new Dimension(300, 40));
		bottom.setBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.gray));

		bottom.add(createDisableBox(listener));
		bottom.add(Box.createHorizontalGlue());
		bottom.add(createRemoveButton(listener));

		this.add(bottom);

		return bottom;
	}

	private void addSliderTitle(String labelText) {
		Font labelFont = new Font("Arial", Font.PLAIN, 14);

		JPanel sliderLabelPanel = new JPanel();
		sliderLabelPanel.setMaximumSize(new Dimension(400, 30));
		sliderLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		sliderLabelPanel.setBackground(Color.white);

		JLabel sliderLabel = new JLabel(labelText, JLabel.LEFT);
		sliderLabel.setFont(labelFont);
		sliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderLabel.setVisible(true);

		sliderLabelPanel.add(sliderLabel);

		this.add(sliderLabelPanel);
	}

	public JSlider createSlider(JsonObject json) {

		Border sliderBorder = new EmptyBorder(5, 5, 5, 5);

		addSliderTitle(json.getString("title"));

		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

		JsonArray array = json.getJsonArray("scale");

		String valueType = json.getString("value_type");

		JSlider slider = null;
		if (valueType.equals("integer")) {
			int minValue = array.getInt(0);
			int maxValue = array.getInt(1);
			int initValue = json.getInt("default");

			slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue,
					initValue);
			slider.setName(json.getString("name"));
			slider.setToolTipText(String.valueOf(slider.getValue()));
			slider.setMinorTickSpacing(maxValue / 4);
			// slider.setMajorTickSpacing(maxValue / 4);

		} else if (valueType.equals("float")) {

			// every value is supplied by 10 to be integer for slider
			int minValue = array.getInt(0) * 100;
			int maxValue = array.getInt(1) * 100;

			double initValue = json.getJsonNumber("default").doubleValue() * 100;
			double delta = (maxValue - minValue) / 100;

			for (int i = 0; i <= maxValue; i++) {

				if ((i % 20) == 0) {

					labelTable.put(new Integer(i),
							new JLabel(String.valueOf(i * delta / 100)));

				}
			}

			slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue,
					new Double(initValue).intValue());
			slider.setMinorTickSpacing(maxValue / 4);
			slider.setName(json.getString("name"));
			slider.setToolTipText(String.valueOf((double) slider.getValue() / 100));

		}

		// if (!labelTable.isEmpty()) {
		// slider.setLabelTable(labelTable);
		// }

		slider.setBackground(this.getBackground());
		slider.setBorder(sliderBorder);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		this.add(slider);

		return slider;
	}

	public void setFilterId(UID filterId) {
		this.filterId = filterId;
	}

	public UID getFilterId() {
		return filterId;
	}

	public int getNeededHeight() {
		return neededHeight;
	}

	public void setNeededHeight(int neededHeight) {
		this.neededHeight = neededHeight;
	}

}