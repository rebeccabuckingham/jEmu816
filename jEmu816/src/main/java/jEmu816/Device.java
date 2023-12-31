package jEmu816;

// all pieces parts of a machine have to extend Device, except the Cpu.

public abstract class Device {
	private final String name;
	protected final int baseAddress;
	protected final int addressSize;

	public abstract int getByte(int addr);
	public abstract void setByte(int addr, int byteValue);

	public Device(String name, int baseAddress, int addressSize) {
		this.name = name;
		this.baseAddress = baseAddress;
		this.addressSize = addressSize;
	}

	public String getName() {
		return name;
	}

	public int getBaseAddress() {
		return baseAddress;
	}
	public int getAddressSize() {
		return addressSize;
	}
	public int getMaxAddress() {
		return baseAddress + addressSize - 1;
	}

	public int effectiveAddress(int addr) { return addr - baseAddress; }

	public String toString() {
		return "device: " + getName() + " start: " +
			Util.fullAddressToHex(baseAddress) + " last: " +
			Util.fullAddressToHex(getMaxAddress());
	}
}
