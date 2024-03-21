package jEmu816;

// all pieces parts of a machine have to extend Device, except the Cpu.

public abstract class Device {
	public String name;
	public int baseAddress;
	public int addressSize;
	public Bus bus;
	public Machine machine;

	public Machine getMachine() { return machine; }
	public void setMachine(Machine m) { machine = m; }

	public abstract int getByte(int addr);
	public abstract void setByte(int addr, int byteValue);

	public Device(String name, int baseAddress, int addressSize) {
		this.name = name;
		this.baseAddress = baseAddress;
		this.addressSize = addressSize;
	}

	public void setBaseAddress(int address) { this.baseAddress = address; }
	public void setAddressSize(int size) { this.addressSize = size; }

	public String getName() {
		return name;
	}
	public void setName(String name) { this.name = name; }

	public int getBaseAddress() {
		return baseAddress;
	}
	public int getAddressSize() {
		return addressSize;
	}
	public int getMaxAddress() {
		return baseAddress + addressSize;
	}

	public void reset() { }

	public int ea(int addr) { return addr - baseAddress; }

	public String toString() {
		return "device: " + getName() + " start: " +
			Util.fullAddressToHex(baseAddress) + " last: " +
			Util.fullAddressToHex(getMaxAddress());
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public Bus getBus() {
		return bus;
	}
}
