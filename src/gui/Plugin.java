package gui;

import java.awt.Dimension;

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
		topPanel.add(fm.createFilterGUI());

		frame.setContentPane(topPanel);
		frame.pack();
		frame.setVisible(true);
	}
}













