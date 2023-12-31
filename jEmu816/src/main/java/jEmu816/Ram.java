package jEmu816;

public class Ram extends Device {
	protected int[] memory;

	public Ram(String name, int baseAddress, int addressSize) {
		super(name, baseAddress, addressSize);
		memory = new int[addressSize];
	}

	@Override
	public int readByte(int addr) {
		return memory[addr - baseAddress];
	}

	@Override
	public void writeByte(int addr, int byteValue) {
		memory[addr - baseAddress] = byteValue & 0xff;
	}
}
