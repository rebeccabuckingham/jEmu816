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

public class RefMachine extends Machine {
	private static final Logger logger = LoggerFactory.getLogger(RefMachine.class);
	protected CpuBase cpu;

	public RefMachine() {
		super();

		cpu = new Cpu(this);

		this.addDevice(new Ram("ram 0000", 0x0000, 0x10000));
		fillMemory(this, 0, 0x10000, 0x00);
		setCpu(cpu);

		cpu.reset();
	}

	public static void main(String[] args) {
		RefMachine machine = new RefMachine();

		S28Loader loader = new S28Loader();
		loader.load(machine, "./examples/simple.s28");
		logger.info("f000: \n" + Util.dump(machine, 0xf000, 28));
		logger.info("ffe0: \n" + Util.dump(machine, 0xffe0, 28));

		logger.info("start emulation");
		machine.cpu.reset();
		logger.info(machine.cpu.toString());
		logger.info("step...");
		machine.cpu.step();
		logger.info(machine.cpu.toString());
	}

}
