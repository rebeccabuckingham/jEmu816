package jEmu816;

import jEmu816.machines.RefMachine;

public class Main {
	public native int intMethod(int n);
	public native String step();
	public native String getStatus();
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
		//if (args.length > 0) {
    //  String filename = args[0];
			String filename = "/Users/rebecca/jEmu816/examples/simple.s28";

      Main main = new Main();

      // emu816 initialization
//      main.init();
//      main.loadFile(filename);
//      main.reset();
//			System.out.println("emu816 status: " + main.getStatus());

			// jemu816 initialization
			S28Loader loader = new S28Loader();
			loader.load(main.m, filename);
			main.m.reset();
			System.out.println("jemu816 status: " + main.m.toString());

//			System.out.println("\n\nfirst step...\n");
//			main.step();
//			System.out.println("emu816 status: " + main.getStatus());

			main.m.getCpu().step();
			System.out.println("jemu816 status: " + main.m.toString());

//			System.out.println(Util.dump(main.m, 0xf000, 0x20));

		  System.out.println("running test...");
			long start = System.nanoTime();
			long instructionCount = main.test(main);
			long end = System.nanoTime();
			long nanoSeconds = end - start;

		  System.out.println("total instruction count: " + instructionCount);
			System.out.println("   total cycles elapsed: " + main.m.cycles);
			System.out.println("      total nanoSeconds: " + nanoSeconds);

			double seconds = (double) nanoSeconds / (double) 1_000_000_000;
			double hz = main.m.cycles / seconds;

		  System.out.println("          total seconds: " + seconds);
			System.out.println("         approximate Hz: " + hz);

    //}
  }

	public long test(Main main) {
		int instructionCount = 0;

		for (int i = 0; i < 40_000_000; i++) {
			instructionCount++;

			//main.step();
			main.m.getCpu().step();

			//String controlStatus = main.getStatus();
			//String testStatus = main.m.toString();

			//System.out.println("[" + i + "] control: " + controlStatus);
			//System.out.println("[" + i + "] test:    " + testStatus);

			if (main.m.getCpu().stopped) {
				System.out.println("*** Test CPU Stopped! ***");
				break;
			}

			//if (!controlStatus.equals(testStatus)) {
			//	System.out.println("*** Test Error! ***");
			//	break;
			//}
		}
		return instructionCount;
	}
}
