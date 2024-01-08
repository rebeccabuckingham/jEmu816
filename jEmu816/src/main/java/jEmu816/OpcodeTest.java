package jEmu816;

import jEmu816.machines.TestMachine;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpcodeTest {
	private static final Logger logger = LoggerFactory.getLogger(OpcodeTest.class);

	public static void main(String[] args) throws Exception {
		TestMachine machine;

		ArgumentParser parser = ArgumentParsers.newFor("Main")
			.build()
			.description("65816 Emulator Tester");

		parser.addArgument("filename")
			.type(String.class)
			.help("filename of tests to run");

		logger.info("creating testMachine.");
		machine = new TestMachine();
		logger.info("testMachine created.");


	}
}
