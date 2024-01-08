package jEmu816;

public class NativeWrapper {
	// this file just serves as a wrapper for emu816 methods
	public native int isStopped();
	public native String step();
	public native String getStatus();
	public native void loadFile(String filename);
	public native void reset();
	public native void init();
	public native void setByte(int addr, int value);
	public native int getByte(int addr);
	public native void resetCycles();

	public native void setP(int value);
	public native int getP();
	public native void setE(int value);

	// new
	public native void setPBR(int value);
	public native void setPC(int value);
	public native void setSP(int value);
	public native void setDBR(int value);
	public native void setDP(int value);
	public native void setA(int value);
	public native void setX(int value);
	public native void setY(int value);




	static {
		System.loadLibrary("emu816");
	}

	public NativeWrapper() {

	}
}
