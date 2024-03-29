/*
 * Copyright (c) 2016 Seth J. Morabito <web@loomcom.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package jEmu816.devices;

import jEmu816.Device;
import jEmu816.DeviceChangeListener;


import java.util.ArrayList;

/**
 * Simulation of a 6545 CRTC and virtual CRT output.
 */
public class Crtc extends Device {
	// Memory locations in the CRTC address space
	public static final int REGISTER_SELECT = 0;
	public static final int REGISTER_RW = 1;

	// Registers
	public static final int HORIZONTAL_DISPLAYED = 1;
	public static final int VERTICAL_DISPLAYED = 6;
	public static final int MODE_CONTROL = 8;
	public static final int SCAN_LINE = 9;
	public static final int CURSOR_START = 10;
	public static final int CURSOR_END = 11;
	public static final int DISPLAY_START_HIGH = 12;
	public static final int DISPLAY_START_LOW = 13;
	public static final int CURSOR_POSITION_HIGH = 14;
	public static final int CURSOR_POSITION_LOW = 15;


	/*
	 * These will determine how the Character ROM is decoded,
	 * and are Character ROM dependent.
	 */

	// R1 - Horizontal Displayed
	private int horizontalDisplayed;

	// R6 - Vertical Displayed
	private int verticalDisplayed;

	// R9 - Scan Lines: Number of scan lines per character, including spacing.
	private int scanLinesPerRow;

	// R10 - Cursor Start / Cursor Mode
	private int cursorStartLine;
	private boolean cursorEnabled;
	private int cursorBlinkRate;

	// R11 - Cursor End
	private int cursorStopLine;

	// R12, R13 - Display Start Address: The starting address in the video RAM of the displayed page.
	private int startAddress;

	// R14, R15 - Cursor Position
	private int cursorPosition;

	// The size, in bytes, of a displayed page of characters.
	private int pageSize;

	private int currentRegister = 0;

	// Status bits
	private boolean rowColumnAddressing = false;
	private boolean displayEnableSkew = false;
	private boolean cursorSkew = false;

	private Ram memory;

	private final ArrayList<DeviceChangeListener> deviceChangeListeners;

	public Crtc(int deviceAddress, Device memory) {
		super("CRTC", deviceAddress, 2);
		this.memory = (Ram) memory;
		this.deviceChangeListeners = new ArrayList<>();

		// Defaults
		this.horizontalDisplayed = 80;
		this.verticalDisplayed = 25;
		this.scanLinesPerRow = 9;
		this.cursorStartLine = 0;
		this.cursorStopLine = 7;
		this.startAddress = memory.getBaseAddress();
		this.cursorPosition = startAddress;
		this.pageSize = horizontalDisplayed * verticalDisplayed;
		this.cursorEnabled = true;
		this.cursorBlinkRate = 500;
	}

	public int getVideoRamAddress() {
		return memory.baseAddress;
	}

	public int getVideoRamAddressSize() {
		return memory.addressSize;
	}


	@Override
	public void setByte(int address, int data) {
		address = address - baseAddress;
		switch (address) {
		case REGISTER_SELECT:
			setCurrentRegister(data);
			break;
		case REGISTER_RW:
			writeRegisterValue(data);
			break;
		}
	}

	@Override
	public int getByte(int address) { //}, boolean cpuAccess)  {
		address = address - baseAddress;
		switch (address) {
		case REGISTER_RW:
			switch (currentRegister) {
			case CURSOR_POSITION_LOW:
				return cursorPosition & 0xff;
			case CURSOR_POSITION_HIGH:
				return cursorPosition >> 8;
			default:
				return 0;
			}
		default:
			return 0;
		}
	}

	public int getCharAtAddress(int address) {
		return memory.getByte(address);
	}

	public int getHorizontalDisplayed() {
		return horizontalDisplayed;
	}

	public int getVerticalDisplayed() {
		return verticalDisplayed;
	}

	public int getScanLinesPerRow() {
		return scanLinesPerRow;
	}

	public int getCursorStartLine() {
		return cursorStartLine;
	}

	public int getCursorStopLine() {
		return cursorStopLine;
	}

	public int getCursorBlinkRate() {
		return cursorBlinkRate;
	}

	public boolean isCursorEnabled() {
		return cursorEnabled;
	}

	public int getStartAddress() {
		return startAddress;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean getRowColumnAddressing() {
		return rowColumnAddressing;
	}

	public boolean getDisplayEnableSkew() {
		return displayEnableSkew;
	}

	public boolean getCursorSkew() {
		return cursorSkew;
	}

	private void setCurrentRegister(int registerNumber) {
		this.currentRegister = registerNumber;
	}

	private void writeRegisterValue(int data) {
		int oldStartAddress = startAddress;
		int oldCursorPosition = cursorPosition;

		switch (currentRegister) {
		case HORIZONTAL_DISPLAYED:
			horizontalDisplayed = data;
			pageSize = horizontalDisplayed * verticalDisplayed;
			break;
		case VERTICAL_DISPLAYED:
			verticalDisplayed = data;
			pageSize = horizontalDisplayed * verticalDisplayed;
			break;
		case MODE_CONTROL:
			rowColumnAddressing = (data & 0x04) != 0;
			displayEnableSkew = (data & 0x10) != 0;
			cursorSkew = (data & 0x20) != 0;
			break;
		case SCAN_LINE:
			scanLinesPerRow = data;
			break;
		case CURSOR_START:
			cursorStartLine = data & 0x1f;
			// Bits 5 and 6 define the cursor mode.
			int cursorMode = (data & 0x60) >> 5;
			switch (cursorMode) {
			case 0:
				cursorEnabled = true;
				cursorBlinkRate = 0;
				break;
			case 1:
				cursorEnabled = false;
				cursorBlinkRate = 0;
				break;
			case 2:
				cursorEnabled = true;
				cursorBlinkRate = 500;
				break;
			case 3:
				cursorEnabled = true;
				cursorBlinkRate = 1000;
				break;
			}
			break;
		case CURSOR_END:
			cursorStopLine = data & 0x1f;
			break;
		case DISPLAY_START_HIGH:
			startAddress = ((data & 0xff) << 8) | (startAddress & 0x00ff);
			break;
		case DISPLAY_START_LOW:
			startAddress = ((data & 0xff) | (startAddress & 0xff00));
			break;
		case CURSOR_POSITION_HIGH:
			cursorPosition = ((data & 0xff) << 8) | (cursorPosition & 0x00ff);
			break;
		case CURSOR_POSITION_LOW:
			cursorPosition = (data & 0xff) | (cursorPosition & 0xff00);
			break;
		default:
			break;
		}

		if (startAddress + pageSize > memory.getMaxAddress()) {
			startAddress = oldStartAddress;
			throw new RuntimeException("Cannot draw screen starting at selected address.");
		}

		if (cursorPosition > memory.getMaxAddress()) {
			cursorPosition = oldCursorPosition;
			throw new RuntimeException("Cannot position cursor past end of memory.");
		}

		notifyListeners();
	}

	public void registerListener(DeviceChangeListener listener) {
		deviceChangeListeners.add(listener);
	}

	public void notifyListeners() {
		for (DeviceChangeListener listener : deviceChangeListeners) {
			listener.deviceStateChanged();
		}
	}

//	VideoWindow videoWindow;
//			try {
//		videoWindow = new VideoWindow(this, 2, 2);
//	} catch (IOException e) {
//		throw new RuntimeException(e);
//	}

//	public void createAndShowGui() {
//		try {
//			VideoWindow videoWindow = new VideoWindow(this, 2, 2);
//			videoWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			videoWindow.pack();
//			videoWindow.setVisible(true);
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
//	}
//
//	public void showVideoWindow() {
//		System.out.println("trying to create videoWindow");
////		SwingUtilities.invokeLater(new Runnable() {
////			@Override
////			public void run() {
//				createAndShowGui();
////			}
////		});

	}

