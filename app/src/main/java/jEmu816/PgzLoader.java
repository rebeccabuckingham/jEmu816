package jEmu816;

/*
 load a .PGZ format file
 format:

 Header = "Z" character.
 Rest of file = Segments, one after another, until we hit EOF:
	address: 3 bytes, little endian.
	length: 3 bytes, little endian.
	data: [length] bytes.
 */

import jEmu816.machines.RefMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PgzLoader {
	private static final Logger logger = LoggerFactory.getLogger(RefMachine.class);

	private static void hexDump(byte[] array, int startIndex, int count) {
		String line = String.format("%06x:", startIndex);
		for (int i = 0; i < count; i++) {
			if (startIndex + i < array.length)
				line += String.format(" %02x", array[startIndex + i]);
		}
		logger.debug(line);
	}

	private static int getAddr24(byte[] content, int pointer) {
		int a1 = (int) (content[pointer] & 0xFF);
		int a2 = (int) (content[pointer + 1] & 0xFF);
		int a3 = (int) (content[pointer + 2] & 0xFF);
		return a1 + (a2 * 256) + (a3 * 65536);
	}

	public static int loadSegment(byte[] content, int pointer, Machine m) {
		Bus bus = m.getBus();
		int address = getAddr24(content, pointer);  pointer += 3;
		int length = getAddr24(content, pointer);  pointer += 3;

		String text = String.format("loadSegment address: %06x, length: %d\n", address, length);
		logger.debug(text);

		for (int i = 0; i < length; i++) {
			bus.setByte(address + i,
				(int) (content[pointer + i] & 0xFF)
				);
		}

		pointer += length;

		return pointer;
	}


	public static void loadPgz(String filename, Machine m) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(filename));
		logger.debug("content size is: " + content.length);
		hexDump(content, 0, 16);

		if (content[0] != 0x5a) {
			throw new IOException(filename + " file doesn't start with a 'Z'!");
		}

		int pointer = 1;

		int segment = 0;
		while (pointer < content.length) {
			pointer = loadSegment(content, pointer, m);
			logger.debug("after loading segment " + segment + ", pointer is now: " + pointer);
			segment++;
		}
	}
}
