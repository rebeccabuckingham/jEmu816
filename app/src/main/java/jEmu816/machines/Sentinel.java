package jEmu816.machines;

import jEmu816.Keyboard;
import jEmu816.Machine;
import jEmu816.Ram;
import jEmu816.vera.VeraDevice;

public class Sentinel extends Machine {
	public Sentinel() {
		super();
		setName("Sentinel");
		setHasVera(true);

		Ram ram = new Ram("ram", 0x0, 0xdf00);
		bus.addDevice(ram);

		VeraDevice vera = new VeraDevice(0xdf000);
		vera.videoInit(2, 1, 8);
		setVera(vera);
		bus.addDevice(vera);

		// TODO: add code to vera (somehow) to add characters to the keyboard.
		Keyboard keyboard = new Keyboard("keyboard", 0xdf20);
		bus.addDevice(keyboard);

		Ram ram2 = new Ram("ram2", 0xe000, 0x200000);
		bus.addDevice(ram2);

		bus.getCpu().reset();
	}
}
