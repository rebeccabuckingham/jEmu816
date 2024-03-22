package jEmu816.machines;

import jEmu816.devices.Crtc;
import jEmu816.devices.Keyboard;
import jEmu816.Machine;
import jEmu816.devices.Ram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrtcMachine extends Machine {
	private static final Logger logger = LoggerFactory.getLogger(CrtcMachine.class);

	public CrtcMachine() {
		super();

		setName("CrtcMachine");

		// 				ram: $00:0000 - $00:d6ff
		//  video ram: $00:d700 - $00:ddff
		//       crtc: $00:df00 - $00:df01
		//   keyboard: $00:df02 - $00:df03
    //        rom: $00:e000 - $00:ffff
		//   high ram: $01:0000 - $1f:ffff

		// note that this differs from the Sentinel machine in that
		// the Sentinel has RAM all the way up to 0xdf00.

		// ram up to the I/O area
		Ram ram = new Ram("ram", 0x0, 0xd700);
		bus.addDevice(ram);

		Keyboard keyboard = new Keyboard("KEYBOARD", 0xdf02);
		bus.addDevice(keyboard);

		// videoRam has to be in its own device
		Ram videoRam = new Ram("videoRam", 0xd700, 0x800);
		bus.addDevice(videoRam);

		// this is how we attach the Crtc.
		hasCrtc = true;
		setCrtc(new Crtc(0xdf00, videoRam));
		bus.addDevice(getCrtc());

		Ram rom = new Ram("rom", 0xe000, 0x2000);
		bus.addDevice(rom);

		// ram upto 2M.
		Ram ram3 = new Ram("ram3", 0x10000, 0x1f0000);
		bus.addDevice(ram3);

		bus.getCpu().reset();
	}
}
