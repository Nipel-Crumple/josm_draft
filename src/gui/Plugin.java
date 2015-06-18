package gui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Plugin {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Filters");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel topPanel = new JPanel();

		FiltersManager fm = new FiltersManager();
		topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		topPanel.setPreferredSize(new Dimension(610, 900));
		List<JPanel> panels = fm.createFilterPanels();
		for (JPanel panel : panels) {
			topPanel.add(panel);
		}

		frame.setContentPane(topPanel);
		frame.pack();
		frame.setVisible(true);
	}
}













