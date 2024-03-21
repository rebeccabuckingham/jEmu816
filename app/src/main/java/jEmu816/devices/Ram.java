package jEmu816.devices;

import jEmu816.Device;


import java.util.Arrays;

public class Ram extends Device {
	protected int[] memory;

	public Ram(String name, int baseAddress, int addressSize) {
		super(name, baseAddress, addressSize);
		memory = new int[addressSize];
	}

	public void fillMemory(int v) {
		Arrays.fill(memory, v & 0xff);
	}

	@Override
	public int getByte(int addr) {
		return memory[addr - baseAddress];
	}

	@Override
	public void setByte(int addr, int byteValue) {
		memory[addr - baseAddress] = byteValue & 0xff;
	}

	public void setByteRaw(int addr, int byteValue) { memory[addr] = byteValue & 0xff; }
}
