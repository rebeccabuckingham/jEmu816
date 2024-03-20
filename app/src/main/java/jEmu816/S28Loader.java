package jEmu816;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class S28Loader {
	private static final Logger logger = LoggerFactory.getLogger(S28Loader.class);

	private int toNybble(char ch)	{
		if ((ch >= '0') && (ch <= '9')) return (ch - '0');
		if ((ch >= 'A') && (ch <= 'F')) return (ch - 'A' + 10);
		if ((ch >= 'a') && (ch <= 'f')) return (ch - 'a' + 10);
		return (0);
	}

	private int toByte(String str)
	{
		int	h = toNybble(str.charAt(offset++)) << 4;
		int	l = toNybble(str.charAt(offset++));

		return (h | l);
	}

	private int toWord(String str)
	{
		int	h = toByte(str) << 8;
		int	l = toByte(str);

		return (h | l);
	}

	private int toAddr(String str)	{
		int h = toByte(str) << 16;
		int m = toByte(str) << 8;
		int l = toByte(str);

		return (h | m | l);
	}

	private int offset = 0;

	private void _load(Machine m, String filename) {
		logger.info("loading: " + filename);
		Bus bus = m.getBus();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			reader.lines().forEach(line -> {
				if (line.startsWith("S")) {
					offset = 2;
					if (line.charAt(1) == '1') {
						int count = toByte(line);
						int addr = toWord(line);
						count -= 3;
						while (count-- > 0) {
							bus.setByte(addr++, toByte(line));
						}
					} else if (line.charAt(1) == '2') {
						int count = toByte(line);
						int addr = toAddr(line);
						count -=4;
						while (count-- > 0) {
							bus.setByte(addr++, toByte(line));
						}
					}
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void load(String filename, Machine m) {
		logger.info("loading: " + filename);
		S28Loader loader = new S28Loader();

		loader._load(m, filename);
	}

	private S28Loader() {

	}
}
