package jEmu816;

import jEmu816.machines.RefMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleConsole.ConsoleDevice;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public native int isStopped();
	public native String step();
	public native String getStatus();
	public native void loadFile(String filename);
	public native void reset();
	public native void init();

	public RefMachine m;

	boolean wantConsole = false;
	boolean runJEmu816 = false;
	boolean runEmu816 = false;
	boolean comparisonMode = false;
	String runFilename;

	int startAddress = -1;

	// for testing support
	static {
		System.loadLibrary("emu816");
	}

	public Main() {
		// initialize emu816
		m = new RefMachine();
//		consoleDevice = new ConsoleDevice((int) 0xD000, 4);
//		bus.add(consoleDevice);
	}

	public long runCode() {
		int instructionCount = 0;

		System.out.println("filename: " + runFilename);
		System.out.println("runEmu816: " + runEmu816);
		System.out.println("runJEmu816: " + runJEmu816);
		System.out.println("comparisonMode: " + comparisonMode);
		System.out.println("startAddress: " + startAddress);

		if (startAddress != -1) {
			m.getCpu().pc = startAddress;
		}

		if (m.getCpu().trace) {
			m.getCpu().traceInstruction();
		}

		while (true) {
			instructionCount++;

			if (runEmu816 || comparisonMode)
				step();

			if (runJEmu816 || comparisonMode)
				m.getCpu().step();

			if (comparisonMode) {
				String controlStatus = getStatus();
				String testStatus = m.toString();

				System.out.println("control: " + controlStatus);
				System.out.println("   test:    " + testStatus);

				if (!controlStatus.equals(testStatus)) {
					System.out.println("*** Test Error! ***");
					break;
				}
			}

			if ((comparisonMode || runJEmu816) && m.getCpu().stopped) {
				System.out.println("*** jEmu816 CPU Stopped! ***");
				break;
			}

			if ((comparisonMode || runEmu816) && isStopped() != 0) {
				System.out.println("*** Emu816 CPU Stopped! ***");
				break;
			}
		}
		return instructionCount;
	}

	public static void main(String[] args) throws Exception {
		Main main = new Main();

		if (args.length > 0) {
			main.runFilename = args[0];
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-emu816"))
					main.runEmu816 = true;
				if (args[i].equalsIgnoreCase("-jemu816"))
					main.runJEmu816 = true;
				if (args[i].equalsIgnoreCase("-compare"))
					main.comparisonMode = true;
				if (args[i].equalsIgnoreCase("-console"))
					main.wantConsole = true;
				if (args[i].equalsIgnoreCase("-startAddress")) {
					i++;
					main.startAddress = Integer.parseInt(args[i], 16);
				}
			}

			if (!main.comparisonMode && !main.runEmu816)
				main.runJEmu816 = true;

			if (main.wantConsole) {
				logger.info("creating console...");
				var consoleDevice = new ConsoleDevice((int) 0xD000, 4);
				main.m.addDevice(consoleDevice);
				consoleDevice.showGUI(consoleDevice);
			}

			// emu816 initialization
			if (main.runEmu816 || main.comparisonMode) {
				main.init();
				main.loadFile(main.runFilename);
				main.reset();
			}

			boolean fileNameError = false;

			// jemu816 initialization
			logger.info("run filename is: " + main.runFilename);
			if (main.runFilename.endsWith(".s28")) {
				logger.info("using S28Loader.");
				S28Loader loader = new S28Loader();
				loader.load(main.m, main.runFilename);
			} else if (main.runFilename.endsWith(".pgz")) {
				logger.info("using PgzLoader.");
				PgzLoader.loadPgz(main.runFilename, main.m);
			} else if (main.runFilename.endsWith(".prg")) {
				logger.info("using PrgLoader.");
				PrgLoader.loadPrg(main.runFilename, main.m);
			} else {
				logger.info("wtf?");
				fileNameError = true;
			}

			if (!fileNameError) {
				main.m.reset();

				System.out.println("running...");



				long start = System.currentTimeMillis();
				long instructionCount = main.runCode();
				long end = System.currentTimeMillis();

				long cycles = main.m.cycles;
				double seconds = (double) (end - start) / (double) 1000;
				double cps = (double) cycles / seconds;

				double mhz = cps / 1_000_000;

				System.out.println("total instructions executed: " + instructionCount);
				System.out.println("            seconds elapsed: " + seconds);
				System.out.println("            approximate Mhz: " + mhz);

				// test - remove
				// System.out.println(Util.dump(main.m, 0xc000, 20));

			} else {
				System.out.println("You must specify either a s28 or pgz file to run!");
			}
		} else {
			System.out.println("Usage: ");
			System.out.println("[filename.s28 | filename.pgz] <-emu816> <-jemu816> <-compare> <-console>");
			System.out.println("-emu816:  use emu816 emulation for cpu.");
			System.out.println("-jemu816: use jemu816 emulation for cpu.");
			System.out.println("-compare: compare two emulators.");
			System.out.println("-console: create simple console.");
			System.out.println("\nDefault is to run on jemu816 with no console.");

			System.out.println();
			System.out.println("note: emulation ends and speed is estimated when WDM instruction ");
			System.out.println("with $ff signature byte is executed.");
		}
	}

}
