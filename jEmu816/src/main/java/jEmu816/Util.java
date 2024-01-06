package jEmu816;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jEmu816.cpu.Cpu;
import jEmu816.cpu.CpuBase;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	public static int low(int value) { return value & 0xff; }
	public static int high(int value) { return (value >> 8) & 0xff; }
	public static int bank(int bank) { return bank << 16; }
	public static int join16(int l, int h) { return l | (h << 8); }
	public static int join(int bank, int addr) { return bank(bank) | addr; }
	public static int swap(int word) { return (word >> 8 | ((word & 0xff) << 8)) ; }

	public static String toHex(int value, int digits) {
		String fmt = "%0" + digits + "x";
		return String.format(fmt, value);
	}

	public static String fullAddressToHex(int addr) {
		int bank = (addr >> 16) & 0xff;
		int addrInBank = addr & 0xffff;

		return toHex(bank, 2) + ":" + toHex(addrInBank, 4);
	}

	public static void fillMemory(Machine m, int addr, int length, int value) {
		for (int i = 0; i < length; i++) {
			m.setByte(addr + i, value);
		}
	}

	private static final int dumpLineSize = 16;

	private static String dumpRow(Machine machine, int address, int count) {
		count = Math.min(count, dumpLineSize);

		String row = fullAddressToHex(address) + " ";
		String ascii = "";

		for (int i = 0; i < count; i++) {
			int b = machine.getByte(address + i);
			row += toHex(b, 2) + " ";

			if (b >= 0x20 && b <= 0x7f) {
				ascii += (char) (b & 0xff);
			} else {
				ascii += '.';
			}
		}

		return row + ascii + "\n";
	}

	public static String dump(Machine m, int address, int count) {
		String dumpString = "";
		for (int i = address; i < (address + count); i += dumpLineSize) {
			dumpString += dumpRow(m, i, count);
		}

		return dumpString;
	}
}
