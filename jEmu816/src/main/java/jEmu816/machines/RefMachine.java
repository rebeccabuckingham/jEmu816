package jEmu816.machines;

import static jEmu816.Util.*;


import jEmu816.S28Loader;
import jEmu816.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jEmu816.Machine;
import jEmu816.Ram;
import jEmu816.cpu.Cpu;
import jEmu816.cpu.CpuBase;
import simpleConsole.ConsoleDevice;

public class RefMachine extends Machine {
	private static final Logger logger = LoggerFactory.getLogger(RefMachine.class);
	protected CpuBase cpu;

	ConsoleDevice consoleDevice;

	public RefMachine() {
		super();

		cpu = new Cpu(this);

		// 00:0000 - 00:cfff - ram
		// 00:d000 - 00:dfff - reserved for i/o
		// 00:e000 - 00:ffff - ram

		this.addDevice(new Ram("ram 0000", 0x0000, 0xD000));
		this.addDevice(new Ram("ram e000", 0xe000, 0x2000));

		fillMemory(this, 0, 0xD000, 0x00);
		setCpu(cpu);


		cpu.reset();
	}

}
