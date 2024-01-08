package jEmu816;

public class NativeWrapper {
	// this file just serves as a wrapper for emu816 methods
	public native int isStopped();
	public native String step();
	public native String getStatus();
	public native void loadFile(String filename);
	public native void reset();
	public native void init();

	static {
		System.loadLibrary("emu816");
	}

	public NativeWrapper() {

	}
}
