package jEmu816.machines;

import jEmu816.Bus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jEmu816.Machine;
import jEmu816.devices.Ram;

public class RefMachine extends Machine {
	private static final Logger logger = LoggerFactory.getLogger(RefMachine.class);

	public RefMachine() {
		super();

		setName("RefMachine");

		Bus bus = getBus();

		// 00:0000 - 00:cfff - ram
		// 00:d000 - 00:dfff - reserved for i/o
		// 00:e000 - 00:ffff - ram

		//this.addDevice(new Ram("ram 0000", 0x0000, 0xD000));
		//this.addDevice(new Ram("ram e000", 0xe000, 0x2000));

		Ram highCode = new Ram("highCode", 0x010000, 0x8000);
		Ram highData = new Ram("highData", 0x018000, 0x8000);
		Ram lowCode =  new Ram("lowCode", 0x00E000, 0x2000);
		Ram lowData =  new Ram("lowData", 0x00D100, 0x0F00);
		Ram base = new Ram("base", 0x000000, 0xd000);

		// not adding i/o here.

		bus.addDevice(highCode);
		bus.addDevice(highData);
		bus.addDevice(lowCode);
		bus.addDevice(lowData);
		bus.addDevice(base);

		bus.getCpu().reset();
	}
}
