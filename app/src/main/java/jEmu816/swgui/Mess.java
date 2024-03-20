package jEmu816.swgui;

import javax.swing.*;

public class Mess {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Hello World Swing");

		// Set the size of the frame
		frame.setSize(300, 200);

		// Set the default close operation
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create a new JLabel with the text "Hello, World!"
		JLabel label = new JLabel("Hello, World!");

		// Add the label to the frame
		frame.getContentPane().add(label);

		// Center the frame on the screen
		frame.setLocationRelativeTo(null);

		// Make the frame visible
		frame.setVisible(true);
	}
}
