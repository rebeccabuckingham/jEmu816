package jEmu816;

import jEmu816.cpu.Cpu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;

public class Bus {
	private static final Logger logger = LoggerFactory.getLogger(Bus.class);

	public Cpu cpu;

	private ArrayList<Device> devices;

	public Cpu getCpu() {
		return cpu;
	}

	public void setCpu(Cpu cpu) {
		this.cpu = cpu;
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}

	public void addDevice(Device d) {
		devices.add(d);
		d.setBus(this);
	}

	public Device findDeviceByAddress(int addr) {
		Device selectedDevice = devices.stream()
			.filter(d -> addr >= d.getBaseAddress() && addr < d.getMaxAddress())
			.findFirst()
			.orElse(null);

		/*if (addr == 0xd011) {
			String name = selectedDevice == null ? "(null)" : selectedDevice.getName();
			System.out.println("address is CHAR_GOT, device is: " + name);
		}*/

		if (selectedDevice == null) {
			logger.warn("No device found for address: " + String.format("%06x", addr));
			return null;
		} else {
			return selectedDevice;
		}
	}

	public Device findDeviceByName(String name) {
		Device selectedDevice = devices.stream()
			.filter(d -> d.getName().equals(name))
			.findFirst()
			.orElse(null);

		return selectedDevice;
	}

	public Device findDeviceByClass(Class clazz) {
		Device selectedDevice = devices.stream()
			.filter(d -> d.getClass() == clazz)
			.findFirst()
			.orElse(null);

		return selectedDevice;
	}

	public int getByte(int addr) {
		Device d = findDeviceByAddress(addr);
		if (d != null) {
			return d.getByte(addr);
		} else {
			return 0xff;
		}
	}

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


	public Bus() {
		devices = new ArrayList<>();
	}
}
