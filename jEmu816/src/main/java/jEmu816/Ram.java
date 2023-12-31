package jEmu816;

public class Ram extends Device {
	protected int[] memory = null;

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

	@Override
	public int readWord(int addr) {
		return Util.join16(readByte(addr), readByte(addr + 1));
	}

	@Override
	public void writeWord(int addr, int wordValue) {
		writeByte(addr, wordValue & 0xff);
		writeByte(addr + 1, Util.swap(wordValue) & 0xff);
	}
}
