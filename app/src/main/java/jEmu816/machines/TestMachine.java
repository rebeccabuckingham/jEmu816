package jEmu816.machines;

import jEmu816.Acia;
import jEmu816.Acia6551;
import jEmu816.Machine;
import jEmu816.Ram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMachine extends Machine {
	private static final Logger logger = LoggerFactory.getLogger(TestMachine.class);

	public TestMachine() {
		super();

		setName("TestMachine");

		hasConsole = true;

		Ram ram = new Ram("ram", 0x0, 0xD000);
		this.getBus().addDevice(ram);

		acia = new Acia6551(0xd000);
		this.getBus().addDevice(acia);

		Ram ram2 = new Ram("ram2", 0xe000, 0x2000);
		this.getBus().addDevice(ram2);

		Ram ram3 = new Ram("ram3", 0x10000, 0x30000);
		this.getBus().addDevice(ram3);

		bus.getCpu().reset();
	}

}
