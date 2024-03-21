package jEmu816;

import jEmu816.cpu.Cpu;
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

	public static void main(String[] args) throws Exception {
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
	}

	private void dumpOptions(Map<String, Object> options) {
		System.out.println("passed in parameters:");
		options.forEach((s, o) -> {
			System.out.println(s + " => " + (o == null ? "(null)" : o.toString()));
		});
	}

	private Machine machine;
	private final int mhz;
	private final String filename;
	private Cpu cpu;

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

		cpu = machine.getBus().getCpu();
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

		debugger = new Debugger(Integer.parseInt((String) options.get("port")));
		debugger.startDebugger();
	}

	// the emulator will run on the main thread
	public void runEmulator() {
		logger.info("runEmulator()");

		// todo all the ugly stuff goes here!
		try {
			Thread.sleep(5000);
		} catch (Exception e) {

		}

		debugger.shutDown();
	}
}
