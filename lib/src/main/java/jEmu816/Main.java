package jEmu816;

public class Main {
	public native int intMethod(int n);

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
