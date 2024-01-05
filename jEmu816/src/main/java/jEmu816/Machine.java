package jEmu816;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import jEmu816.cpu.CpuBase;

import org.slf4j.Logger;

public abstract class Machine implements ByteSource {
	public long cycles;
	public CpuBase cpu;

	private ArrayList<Device> devices;

	private static final Logger logger = LoggerFactory.getLogger(Machine.class);

	public Machine() {
		logger.info("welcome my son, welcome to the machine.");
		this.cycles = 0;
		this.devices = new ArrayList<>();
	}

	public void setCpu(CpuBase cpu) {
		this.cpu = cpu;
	}

	public CpuBase getCpu() {
		return cpu;
	}

	public void setTrace(boolean trace) {
		cpu.trace = trace;
	}

	public void reset() {
    cpu.reset();
  }

	public ArrayList<Device> getDevices() { return devices; }
	public void setDevices(ArrayList<Device> devices) { this.devices = devices; }
	public void addDevice(Device d) { this.devices.add(d); }

	public Device findDeviceByName(String name) {
		return devices.stream()
			.filter(d -> d.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public Device findDeviceByAddress(int addr) {
		Device selectedDevice = devices.stream()
			.filter(d -> addr >= d.getBaseAddress() && addr <= d.getMaxAddress())
			.findFirst()
			.orElse(null);

		if (selectedDevice == null) {
			logger.warn("No device found for address: " + String.format("%06x", addr));
			return null;
		} else {
			return selectedDevice;
		}
	}

	@Override
	public int getByte(int addr) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			return d.getByte(addr);
		} else {
			return 0xff;
		}
	}

	@Override
	public void setByte(int addr, int byteValue) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			d.setByte(addr, byteValue);
		}
	}

	public int getWord(int addr) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			return Util.join16(d.getByte(addr), d.getByte(addr + 1));
		} else {
			return 0xffff;
		}
	}

	public void setWord(int addr, int wordValue) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			d.setByte(addr, wordValue & 0xff);
			d.setByte(addr + 1, Util.swap(wordValue) & 0xff);
		}
	}

	public String toString() {
		return cpu.toString() + " cycles: " + cycles;
	}

	public abstract void handleDevices();
	public abstract void generateInterrupt(int num);

	private boolean enableInterruptProcessing = false;
	private long interruptTimerInterval = 0;

	public void run() {
		long lastInterrupt = System.nanoTime();
		long instructionStart;
		long elapsedTime;

		// todo need number of nanoseconds for one clock cycle
		long nanosecondsPerCycle = 0;

		cpu.stopped = false;
		while (!cpu.stopped) {
			instructionStart = System.nanoTime();
			cpu.step();
			handleDevices();

			// todo add interrupt processing here
			if (enableInterruptProcessing &&
				(System.nanoTime() - lastInterrupt) > interruptTimerInterval) {
				// i = interrupt disable
				if (!cpu.f.i) {
					generateInterrupt(0);
					lastInterrupt = System.nanoTime();
				}
			}

//			// todo wait until elapsedTime >= cpu.cycles * nanosecondsPerCycle
//			do {
//				elapsedTime = System.nanoTime() - instructionStart;
//			} while (elapsedTime < cpu.cycles * nanosecondsPerCycle);
		}
	}
}
