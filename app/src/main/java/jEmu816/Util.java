package jEmu816;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static java.lang.reflect.Array.getShort;

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
			m.getBus().setByte(addr + i, value);
		}
	}

	private static final int dumpLineSize = 16;

	private static String dumpRow(Machine machine, int address, int count) {
		Bus bus = machine.getBus();
		count = Math.min(count, dumpLineSize);

		String row = fullAddressToHex(address) + " ";
		String ascii = "";

		for (int i = 0; i < count; i++) {
			int b = bus.getByte(address + i);
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

	public static int loadFile(Machine m, String filename) throws Exception {
		int address = -1;
		if (filename.endsWith(".pgz")) {
			PgzLoader.loadPgz(filename, m);
		} else if (filename.endsWith(".prg")) {
			address = PrgLoader.loadPrg(filename, m);
		} else if (filename.endsWith(".s28")) {
			S28Loader.load(filename, m);
		}
		return address;
	}

	public static int getShortFromByteArray(byte[] array, int offset) {
		int total = 0;
		total += array[offset] + (array[offset + 1] << 8);
		return total;
	}

	public static int getIntFromByteArray(byte[] array, int offset) {
		int total = 0;
		total += getShortFromByteArray(array, offset);
		total += (getShortFromByteArray(array, offset + 2) << 16);
		return total;
	}

	public static void dumpByteArray(byte[] event) {
		int count = 0;
		System.out.printf("0x0000: ");
		for (int i = 0; i < event.length; i++) {
			System.out.printf("%02x ", event[i]);
			count++;
			if (count == 8) {
				System.out.printf("\n%04x", i);
				count = 0;
			}
		}
	}
}
