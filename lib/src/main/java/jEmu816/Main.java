package jEmu816;

public class Main {
	public native int intMethod(int n);
	public native String step();
	public native void loadFile(String filename);
	public native void reset();
	public native void init();

	// for testing support
	static {
		System.loadLibrary("lib");
	}

	public Main() {

	}

	public static void main(String[] args) {
		Main m = new Main();
		System.out.println("intMethod: " + m.intMethod(10));
  }
}
