package jEmu816.machines;

import jEmu816.Machine;
import jEmu816.Ram;
import jEmu816.cpu.Cpu;
import jEmu816.cpu.CpuBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMachine extends Machine {
	private static final Logger logger = LoggerFactory.getLogger(TestMachine.class);
	protected CpuBase cpu;

	@Override
	public void handleDevices() {

	}

	@Override
	public void generateInterrupt(int num) {

	}

	public TestMachine() {
		super();

		Ram allMemory = new Ram("ram", 0, 16777216);
		addDevice(allMemory);

		cpu = new Cpu(this);
		setCpu(cpu);
		cpu.reset();
	}
}
