package jEmu816;

public interface ByteSource {
	public int getByte(int addr);
	public void setByte(int addr, int byteValue);
}
