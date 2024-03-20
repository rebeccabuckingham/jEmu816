package jEmu816.machines;

import jEmu816.Crtc;
import jEmu816.Keyboard;
import jEmu816.Machine;
import jEmu816.Ram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrtcMachine extends Machine {
	private static final Logger logger = LoggerFactory.getLogger(CrtcMachine.class);

	public CrtcMachine() {
		super();

		setName("CrtcMachine");

		// ram up to the I/O area
		Ram ram = new Ram("ram", 0x0, 0xd000);
		bus.addDevice(ram);

		// I/O Area: $d000-$dfff
		// I/O: Keyboard @ 0xd010 (2 bytes)
		//      Crtc @ 0xd20 (2 bytes)
		Keyboard keyboard = new Keyboard("KEYBOARD", 0xd010);
		bus.addDevice(keyboard);

		// videoRam has to be in its own device
		Ram videoRam = new Ram("videoRam", 0xd800, 0x800);
		bus.addDevice(videoRam);

		// this is how we attach the Crtc.
		hasCrtc = true;
		setCrtc(new Crtc(0xd020, videoRam));
		bus.addDevice(getCrtc());

		// ram upto 256k
		Ram ram3 = new Ram("ram3", 0xe000, 0x32000);
		bus.addDevice(ram3);

		bus.getCpu().reset();
	}
}
