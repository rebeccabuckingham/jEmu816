package jEmu816.devices;

public class Rom extends Ram {
	public Rom(String name, int baseAddress, int addressSize) {
		super(name, baseAddress, addressSize);
	}

	@Override
	public void setByte(int addr, int byteValue) {
		// does nothing
	}

	// for loading from a file or something
	public void setContents(int[] values) {
		memory = values;
	}
}
