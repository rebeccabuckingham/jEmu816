package simpleConsole;

import simpleConsole.gui.ConsoleKeyHandler;
import simpleConsole.ui.Console;
import jEmu816.Device;
import jEmu816.Util;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this is a *really* simple console that
 * provides a way for the 65816 to write to the screen and read from the keyboard
 */

public class ConsoleDevice extends Device {
	private final static int BUFSIZE = 128;
	private final static int CHAR_READY_FLAG = 0;			// memory location that's set when there's input waiting
	private final static int CHAR_OUT = 0;						// memory location to write to screen
	private final static int CURRENT_CHAR = 1;				// get char that's waiting, will *not* move the pointer
	private final static int CHARGOT = 2;

	private static final int DEFAULT_FONT_SIZE = 16;
	private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, DEFAULT_FONT_SIZE);
	private static final int CONSOLE_BORDER_WIDTH = 10;

	public Console console = null;

	public final Stack<Character> keysPressed = new Stack<>();

	public ConsoleDevice(int startAddress, int size) {
		super("Console (" + Util.fullAddressToHex(startAddress) + "," + Util.toHex(size, 2) + ")", startAddress, size);
	}

	public synchronized void addKeyPressed(char c) {
		keysPressed.push(c);
	}

	public synchronized char getKeyPressed() {
		return keysPressed.pop();
	}

	public synchronized boolean charAvailable() {
		return !keysPressed.isEmpty();
	}

	public boolean echoMode = false;
	public boolean noCpuMode = false;

	public AtomicBoolean lock = new AtomicBoolean(true);

	/**
	 * readMemory() in this case is how the emulated machine gets input from the keyboard
	 */
	@Override
	public int getByte(int address) {
		int ea = effectiveAddress(address);
		int value = 0;

		if (ea == CHAR_READY_FLAG) {
			value = (charAvailable() ? 1 : 0);
		} else if (ea == CURRENT_CHAR) {
			if (!charAvailable())
				value = -1;
			else {
				value = keysPressed.peek() & 0xFF;
			}
		}

		return value;
	}

	/**
	 * writeMemory() is how the emulated machine writes to the screen
	 * and how it tells the console it read a character
	 */

	@Override
	public void setByte(int address, int value) {
		int ea = effectiveAddress(address);
		char c = (char) value;

		if (ea == CHAR_OUT) {
			if (c == '\n')
				console.println("");
			else
				console.print(String.valueOf(c));

			console.repaint();
		} else if (ea == CHARGOT) {
			// can dispose of character now
			keysPressed.pop();
		}
	}

	public void createAndShowGUI(final ConsoleDevice consoleDevice) {
		console = new Console(80, 25, DEFAULT_FONT, false);

		JFrame frame = new JFrame("Console");
		frame.addKeyListener(new ConsoleKeyHandler(this));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(console, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {

			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentShown(ComponentEvent e) {
				lock.set(false);
				System.out.println("*** check for console ***");
			}

			@Override
			public void componentHidden(ComponentEvent e) {

			}
		});

	}

	public void handleKeyEcho(char c) {
		if (echoMode) {
			console.print(String.valueOf(c));
			console.repaint();
		}
	}

	public void showGUI(final ConsoleDevice consoleDevice) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(consoleDevice);
			}
		});
	}

}
