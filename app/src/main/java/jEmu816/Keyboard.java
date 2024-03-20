package jEmu816;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Keyboard extends Device {
	private static final Logger logger = LoggerFactory.getLogger(Keyboard.class);

	// TODO: could expand this to have a third register that takes care of modifiers, etc.
	// TODO: will also *probably* want to expand this to give the keyboard the ability to raise an IRQ.

	public static final int CONTROL_REG = 0;
	public static final int CHAR_GOT = 1;

	boolean isCharReady = false;
	char c = 0;

	public Keyboard(String name, int baseAddress) {
		super(name, baseAddress, 2);
	}

	public void setTypedKey(char c) {
		this.c = c;
		isCharReady = true;
	}

	@Override
	public int getByte(int addr) {
		int realAddress = addr - baseAddress;
		if (realAddress == CONTROL_REG) {
			//logger.debug("getByte(CONTROL_REG): isCharReady = " + isCharReady);
			return isCharReady ? 1 : 0;
		} else if (realAddress == CHAR_GOT) {
			//logger.debug("getByte(CHAR_GOT): c = " + c);
			isCharReady = false;
			return c;
		}
		return 0;
	}

	@Override
	public void setByte(int addr, int byteValue) {
		// a way to reset the keyboard.
		int realAddress = addr - baseAddress;
		if (realAddress == CONTROL_REG) {
			if ((byteValue & 0x01) == 0) {
				isCharReady = false;
				c = 0;
			}
		}
	}
}
