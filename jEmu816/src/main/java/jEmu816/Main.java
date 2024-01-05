package jEmu816;

import jEmu816.machines.RefMachine;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


import java.sql.Ref;
import java.util.Map;

public class Main {
	Machine machine;

	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor("Main").build()
			.description("65816 Emulator");
		parser.addArgument("filename")
			.type(String.class)
			.help("name of file to run, can be .S28, .pgz, or .prg.");
		parser.addArgument("--startAddress")
			.type(String.class)
			.setDefault(-1)
			.help("address to begin execution, overrides reset vector.");

		parser.addArgument("-trace")
			.type(Boolean.class)
			.action(Arguments.storeTrue())
			.help("trace execution to log file.");

//		parser.addArgument("--machineType")
//			.type(String.class)
//			.setDefault("RefMachine")
//			.help("machine type to run against.  Currently only RefMachine supported.");

		try {
			Namespace res = parser.parseArgs(args);

			String filename = ((String) res.get("filename")).toLowerCase();
			if (!filename.endsWith(".s28") &&
				!filename.endsWith(".pgz") &&
				!filename.endsWith(".prg")) {
				System.out.println("error: filename must end in .s28, .pgz, or .prg.");
			} else {
				Main main = new Main(res.getAttrs());
				main.machine.run();
			}
		} catch(ArgumentParserException e) {
			parser.handleError(e);
		}
	}

	protected Map<String, Object> options;

	// allow either hex or decimal entry for the start address option
	// hex has to be in 0x1234 format.
	private int getStartAddress() {
		int startAddress;
		String saStr = (String) options.get("startAddress");
		if (saStr.startsWith("0x")) {
			startAddress = Integer.parseInt(saStr.substring(2), 16);
		} else {
			startAddress = Integer.parseInt(saStr);
		}

		return startAddress;
	}

	private void showOptions() {
		options.forEach((s, o) -> {
			System.out.println("map '" + s + "' => " + o.toString() +
													 " (" + o.getClass().getName() + ")");
		});
	}

	public Main(Map<String, Object> options) {
		this.options = options;
		showOptions();

		RefMachine m = new RefMachine();
		machine = m;

		String filename = (String) options.get("filename");
		if (filename.toLowerCase().endsWith(".s28")) {
			// s28Loader
		} else if (filename.toLowerCase().endsWith(".pgz")) {
			// pgzLoader
		} else if (filename.toLowerCase().endsWith(".prg")) {
			// prgLoader
		}

		m.setTrace((Boolean) options.get("trace"));
		m.reset();

		// todo handle startaddress option here.

		// that's it.  main() is already set to call machine.run().
	}
}
