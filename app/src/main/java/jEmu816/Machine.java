package jEmu816;

import java.util.ArrayList;


import jEmu816.cpu.Cpu;
import jEmu816.devices.Acia;
import jEmu816.devices.Crtc;
import jEmu816.devices.vera.VeraDevice;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Machine {
	public Bus bus;

	private Crtc crtc = null;
	private VeraDevice veraDevice = null;
	protected Acia acia = null;

	private static final Logger logger = LoggerFactory.getLogger(Machine.class);

	public Machine() {
		logger.info("welcome my son, welcome to the machine.");
		bus = new Bus();
		bus.setCpu(new Cpu(this, bus, 1000));
	}

	public boolean hasCrtc = false;
	public boolean hasVera = false;
	public boolean hasConsole = false;

	public VeraDevice getVera() {
		return veraDevice;
	}

	public void setVera(VeraDevice veraDevice) {
		this.veraDevice = veraDevice;
	}

	public boolean hasVera() {
		return hasVera;
	}

	public void setHasVera(boolean hasVera) {
		this.hasVera = hasVera;
	}

	public boolean hasCrtc() {
		return hasCrtc;
	}

	public void setHasCrtc(boolean hasCrtc) {
		this.hasCrtc = hasCrtc;
	}

	public Crtc getCrtc() { return crtc; }
	public void setCrtc(Crtc crtc) { this.crtc = crtc; }

	public Acia getAcia() { return acia; }
	public boolean hasAcia() { return acia != null; }


	public Bus getBus() { return bus; }

	public void setTrace(boolean trace) {
		bus.getCpu().trace = trace;
	}

	public void reset() {
    bus.getCpu().reset();
		bus.getDevices().stream().forEach(Device::reset);
  }

	private String name;

	public String getName() {
		if (name == null) {
			name = getClass().getName();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void init() { }

	public ArrayList<Device> getDevices() { return bus.getDevices(); }

	public void handleWDM(int sigByte) {

	}

}
