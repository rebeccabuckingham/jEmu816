package jEmu816;

import jEmu816.cpu.Cpu;
import jEmu816.devices.Keyboard;
import jEmu816.devices.vera.VeraDevice;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;

public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static final String APP_NAME = "jEmu816";

	public static void main(String[] args) {
		try {
			ArgumentParser parser = ArgumentParsers.newFor(APP_NAME).build()
				.defaultHelp(true)
				.description("65c816 System Emulator.");

			parser.addArgument("-m", "--machine")
				.setDefault("jEmu816.machines.Sentinel")
				.help("machine to emulate");

			parser.addArgument("-s", "--speed")
				.setDefault("8")
				.help("Set emulated cpu speed in Mhz");

			parser.addArgument("-f", "--filename")
				.help("Specify a file to load and optinally run.");

			parser.addArgument("-w", "--wait")
				.type(Boolean.class)
				.setDefault(false)
				.help("Do not run automatically, wait for debugger.");

			parser.addArgument("-p", "--port")
				.setDefault("6502")
				.help("port to use for debugging interface");

			parser.addArgument("-a", "--startAddress")
				.help("Set the start address");

			Namespace ns = null;
			try {
				ns = parser.parseArgs(args);
				var app = new App(ns.getAttrs());
				app.runEmulator();
			} catch (ArgumentParserException e) {
				parser.handleError(e);
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dumpOptions(Map<String, Object> options) {
		System.out.println("passed in parameters:");
		options.forEach((s, o) -> {
			System.out.println(s + " => " + (o == null ? "(null)" : o.toString()));
		});
	}

	public Machine machine;
	private final int mhz;
	private final String filename;
	private Bus bus;
	public Cpu cpu;
	private boolean haveVera;
	private VeraDevice veraDevice;
	private Keyboard keyboard;
	private int nanosPerCycle;
	private boolean waitForDebugger = false;
	private Debugger debugger = null;

	@SuppressWarnings("unchecked")
	private void createMachine(String className) throws Exception {
		Class<Machine> machineClass = (Class<Machine>) Class.forName(className);
		var constructor = machineClass.getDeclaredConstructor();
		machine = (Machine) constructor.newInstance();
	}

	public App(Map<String, Object> options) throws Exception {
		dumpOptions(options);

		mhz = Integer.parseInt((String) options.get("speed"));
		createMachine((String) options.get("machine"));

		bus = machine.getBus();
		cpu = bus.getCpu();
		cpu.setNanosPerCycle((int) ((1D / mhz) * 1000));

		// load file and set startAddress
		int startAddress = -1;

		filename = (String) options.get("filename");
		if (filename != null) {
			startAddress = Util.loadFile(machine, filename);
		}

		if (startAddress == -1) {
			var startAddressStr = (String) options.get("startAddress");
			if (startAddressStr != null && !startAddressStr.isBlank()) {
				if (startAddressStr.startsWith("0x")) {
					startAddress = Integer.parseInt(startAddressStr.substring(2), 16);
				} else {
					startAddress = Integer.parseInt(startAddressStr);
				}
			}
		}

		machine.reset();

		if (startAddress > -1) {
			int pc = startAddress & 0xffff;
			int pbr = startAddress >> 16;
			cpu.pc = pc;
			cpu.pbr = pbr;
		}

		debugger = new Debugger(this, Integer.parseInt((String) options.get("port")));
		debugger.startDebugger();

		waitForDebugger = (boolean) options.get("wait");
	}

	boolean shutdown = false;
	boolean running = false;

	public void run() {
		running = true;
	}

	public void stop() {
		running = false;
	}

	public void step() {
		long insStart = System.nanoTime();
		long insEnd;
		int cycles = cpu.step();

		if (haveVera) {
			boolean newFrame = veraDevice.videoStep(cycles);
			if (newFrame) {
				boolean haveEvent = veraDevice.videoUpdate();
				if (haveEvent) {
					processSdlEvent(veraDevice.getSDLEvent(), keyboard);
				}
			}
		}

		do {
			insEnd = System.nanoTime();
		} while (insEnd < (insStart + (nanosPerCycle * cycles)));

	}

	private void processSdlEvent(byte[] sdlEvent, Keyboard keyboard) {

	}

	public boolean handleDebugger() {
		return false;
	}

	// the emulator runs on the main thread
	public void runEmulator() {
		logger.info("runEmulator()");

		haveVera = machine.hasVera();
		veraDevice = machine.getVera();
		keyboard = (Keyboard) bus.findDeviceByClass(Keyboard.class);

		if (haveVera) {
			veraDevice.videoInit(2, 1, mhz);
		}

		shutdown = false;
		running = !waitForDebugger;
		nanosPerCycle = cpu.getNanosPerCycle();

		while (!shutdown) {
			if (running) {
				step();
			}

			// debugger could change running to true, set shutdown to true, call step(), etc.
			shutdown = handleDebugger();
		}

		if (haveVera) {
			veraDevice.videoEnd();
		}

		debugger.shutDown();
	}
}
