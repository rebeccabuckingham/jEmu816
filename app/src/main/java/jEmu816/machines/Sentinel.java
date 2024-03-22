package jEmu816.machines;

import jEmu816.devices.Keyboard;
import jEmu816.Machine;
import jEmu816.devices.Ram;
import jEmu816.devices.vera.VeraDevice;

public class Sentinel extends Machine {
	public Sentinel() {
		super();
		setName("Sentinel");
		setHasVera(true);

		Ram ram = new Ram("ram", 0x0, 0xdf00);
		bus.addDevice(ram);

		VeraDevice vera = new VeraDevice(0xdf000);
		setVera(vera);
		bus.addDevice(vera);

		Keyboard keyboard = new Keyboard("keyboard", 0xdf20);
		bus.addDevice(keyboard);

		Ram rom = new Ram("rom", 0xe000, 0x2000);
		bus.addDevice(rom);

		Ram ram2 = new Ram("ram2", 0x10000, 0x1F0000);
		bus.addDevice(ram2);

		bus.getCpu().reset();
	}
}
