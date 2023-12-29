package jEmu816;

import jEmu816.machines.RefMachine;

public class Main {
	public native int intMethod(int n);
	public native String step();
	public native void loadFile(String filename);
	public native void reset();
	public native void init();

  public RefMachine m;

	// for testing support
	static {
		System.loadLibrary("emu816");
	}

	public Main() {
    // initialize emu816
    m = new RefMachine();

	}






	public static void main(String[] args) {

		if (args.length > 0) {
      String filename = args[0];

      Main main = new Main();

      // jemu816 initialization
      S28Loader loader = new S28Loader();
      loader.load(main.m, filename);
      main.m.reset();

      // emu816 initialization
      main.init();
      main.loadFile(filename);
      main.reset();

      // TODO print status strings from both sides

      // TODO start looping via step() methods for each emulator
      //      - do emu816 first, then jemu816.
      //      - compare status strings after each instruction
      //      -- stop with error when status strings don't match.
      //      - we keep going until emu816 is stopped, and after jemu816
      //        executes one last instruction

    }
  }
}
