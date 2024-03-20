package jEmu816;

import jEmu816.machines.RefMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrgLoader {
	private static final Logger logger = LoggerFactory.getLogger(PrgLoader.class);

	public static int loadPrg(String filename, Machine m) throws IOException {
		Bus bus = m.getBus();
		byte[] content = Files.readAllBytes(Paths.get(filename));
		System.out.println("content size is: " + content.length);

		int addrLo = (content[0] >= 0 ? (int) content[0] : ((int) content[0] + 256));
		int addrHi = (content[1] >= 0 ? (int) content[1] : ((int) content[1] + 256));
		int loadAddress = Util.join16(addrLo, addrHi);

		logger.info("addrHi: " + Util.toHex(addrHi, 2));
		logger.info("addrLo: " + Util.toHex(addrLo, 2));
		logger.info("load address is: " + Util.toHex(loadAddress, 4));

		int addr = loadAddress;
		for (int i = 2; i<content.length; i++) {
			int b = (content[i] >= 0 ? (int) content[i] : ((int) content[i] + 256));
			logger.info("setting " + Util.toHex(addr, 4) + " to " + Util.toHex(b, 2));
			bus.setByte(addr, b);
			addr++;
		}

		System.out.println(Util.dump(m, loadAddress, content.length - 2));

		return loadAddress;
	}

	public static void main(String[] args) throws Exception {
		RefMachine m = new RefMachine();
		loadPrg("/Users/rebecca/65x/asmtest/xatest.prg", m);
		Util.dump(m, 0xc000, 64);
	}
}
