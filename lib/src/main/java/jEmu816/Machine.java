package jEmu816;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import jEmu816.cpu.CpuBase;

import org.slf4j.Logger;

public abstract class Machine {
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

	public int getByte(int addr) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			return d.readByte(addr);
		} else {
			return 0xff;
		}
	}

	public void setByte(int addr, int byteValue) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			d.writeByte(addr, byteValue);
		}
	}

	public int getWord(int addr) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			return d.readWord(addr);
		} else {
			return 0xffff;
		}
	}

	public void setWord(int addr, int wordValue) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			d.writeWord(addr, wordValue);
		}
	}
}
