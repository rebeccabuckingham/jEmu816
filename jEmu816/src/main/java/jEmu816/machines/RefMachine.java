package jEmu816.machines;

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

		//this.addDevice(new Ram("ram 0000", 0x0000, 0xD000));
		//this.addDevice(new Ram("ram e000", 0xe000, 0x2000));

		Ram highCode = new Ram("highCode", 0x010000, 0x8000);
		Ram highData = new Ram("highData", 0x018000, 0x8000);
		Ram lowCode =  new Ram("lowCode", 0x00E000, 0x2000);
		Ram lowData =  new Ram("lowData", 0x00D100, 0x0F00);
		Ram stack = new Ram("stack", 0x00C800, 0x0800);
		Ram base = new Ram("base", 0x000000, 0xC800);

		this.addDevice(highCode);
		this.addDevice(highData);
		this.addDevice(lowCode);
		this.addDevice(lowData);
		this.addDevice(stack);
		this.addDevice(base);

		//fillMemory(this, 0, 0xD000, 0x00);
		setCpu(cpu);

		cpu.reset();
	}

	@Override
	public void handleDevices() {
		// this is where we update the screen, process audio, whatever.
	}

	@Override
	public void generateInterrupt(int num) {
		// not even sure what this entails yet.
	}
}
