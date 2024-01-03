package jEmu816;

import jEmu816.machines.RefMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrgLoader {
	private static final Logger logger = LoggerFactory.getLogger(PrgLoader.class);

	public static void loadPrg(String filename, Machine m) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(filename));
		System.out.println("content size is: " + content.length);

		int addrLo = (content[0] >= 0 ? (int) content[0] : ((int) content[0] + 256));
		int addrHi = (content[1] >= 0 ? (int) content[1] : ((int) content[1] + 256));
		int loadAddress = Util.join16(addrLo, addrHi);

		System.out.println("addrHi: " + Util.toHex(addrHi, 2));
		System.out.println("addrLo: " + Util.toHex(addrLo, 2));
		System.out.println("load address is: " + Util.toHex(loadAddress, 4));

		int addr = loadAddress;
		for (int i = 2; i<content.length; i++) {
			int b = (content[i] >= 0 ? (int) content[i] : ((int) content[i] + 256));
			m.setByte(addr, b);
			addr++;
		}

		System.out.println(Util.dump(m, loadAddress, content.length - 2));
	}

	public static void main(String[] args) throws Exception {
		RefMachine m = new RefMachine();
		loadPrg("/Users/rebecca/65x/asmtest/xatest.prg", m);
		Util.dump(m, 0xc000, 64);
	}
}
