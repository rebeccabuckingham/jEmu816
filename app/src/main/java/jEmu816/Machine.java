package jEmu816;

import java.util.ArrayList;
import java.util.HashMap;


import com.fasterxml.jackson.databind.ObjectMapper;
import jEmu816.cpu.Cpu;
import jEmu816.machines.DefaultMachine;
import jEmu816.swgui.WDMHandler;
import jEmu816.vera.VeraDevice;
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

	WDMHandler WDMHandler;

	public void setWDMHandler(WDMHandler WDMHandler) {
		this.WDMHandler = WDMHandler;
	}

	public void handleWDM(int sigByte) { WDMHandler.handleWDM(sigByte); }

	protected static String quoteString(String str) {
		return '"' + str + '"';
	}

	/*
	JSON support
	toJson() creates:
		{"name":"jEmu816.machines.CrtcMachine","class":"jEmu816.machines.CrtcMachine","hasCrtc":true,"crtcAddress":"0x00d020","videoRamAddress":"0x00dc00","videoRamSize":"0x0400","devices": [{"name":"ram","className":"jEmu816.Ram","baseAddress":"0x000000","addressSize":"0xd000"},{"name":"videoRam","className":"jEmu816.Ram","baseAddress":"0x00dc00","addressSize":"0x0400"},{"name":"ram2","className":"jEmu816.Ram","baseAddress":"0x00e000","addressSize":"0x32000"},{"name":"KEYBOARD","className":"jEmu816.Keyboard","baseAddress":"0x00d010","addressSize":"0x0002"}]}

	fromJson parses out:

		key: "devices" -> (java.util.ArrayList) "[{name=ram, className=jEmu816.Ram, baseAddress=0x000000, addressSize=0xd000}, {name=videoRam, className=jEmu816.Ram, baseAddress=0x00dc00, addressSize=0x0400}, {name=ram2, className=jEmu816.Ram, baseAddress=0x00e000, addressSize=0x32000}, {name=KEYBOARD, className=jEmu816.Keyboard, baseAddress=0x00d010, addressSize=0x0002}]"
		key: "name" -> (java.lang.String) "jEmu816.machines.CrtcMachine"
		key: "hasCrtc" -> (java.lang.Boolean) "true"
		key: "crtcAddress" -> (java.lang.String) "0x00d020"
		key: "videoRamSize" -> (java.lang.String) "0x0400"
		key: "videoRamAddress" -> (java.lang.String) "0x00dc00"
		key: "class" -> (java.lang.String) "jEmu816.machines.CrtcMachine"

	 */

	public String toJson() {
		String json = "{";
		json += quoteString("name") + ":" + quoteString(getName()) + "," + quoteString("class") + ":" + quoteString(getClass().getName());
		json += "," + quoteString("hasCrtc") + ":" + hasCrtc;

		if (hasCrtc) {
			json += "," + quoteString("crtcAddress") + ":" + quoteString("0x" + Util.toHex(crtc.getBaseAddress(), 6));
			json += "," + quoteString("videoRamAddress") + ":" + quoteString("0x" + Util.toHex(crtc.getVideoRamAddress(), 6));
			json += "," + quoteString("videoRamSize") + ":" + quoteString("0x" + Util.toHex(crtc.getVideoRamAddressSize(), 4));
		}

		json += "," + quoteString("devices") + ": [";
		boolean first = true;
		for (Device d: getDevices()) {
			if (first) {
				first = false;
			} else {
				json += ",";
			}
			json += "{" + quoteString("name") + ":" + quoteString(d.getName());
			json += "," + quoteString("className") + ":" + quoteString(d.getClass().getName());
			json += "," + quoteString("baseAddress") + ":" + quoteString("0x" + Util.toHex(d.getBaseAddress(), 6));
			json += "," + quoteString("addressSize") + ":" + quoteString("0x" + Util.toHex(d.getAddressSize(), 4));
			json += "}";
		}
		json += "]";

		json += "}";
		return json;
	}

	@SuppressWarnings("unchecked")
	public static Machine reviveFromJson(Bus bus, String json) throws Exception {
		Machine m = null;

		HashMap<String, Object> props = new ObjectMapper().readValue(json, HashMap.class);

		// create an instance of the machine class
		Class<Machine> machineClass = (Class<Machine>) Class.forName((String) props.get("class"));
		var constructors = machineClass.getConstructors();
		m = (Machine) constructors[0].newInstance();

		m.setName((String) props.get("name"));
		m.bus = bus;

		ArrayList<Device> devices = new ArrayList<>();
		ArrayList<HashMap> propsDevicesList = (ArrayList<HashMap>) props.get("devices");

		for (HashMap hashMap : propsDevicesList) {
			String deviceName = (String) hashMap.get("name");
			String deviceClassName = (String) hashMap.get("className");
			String deviceBaseAddress = (String) hashMap.get("baseAddress");
			String deviceAddressSize = (String) hashMap.get("addressSize");

			// note: some classes may need special handling.
			Class<Device> deviceClass = (Class<Device>) Class.forName(deviceClassName);
			var deviceConstructors = deviceClass.getConstructors();
			Device device = (Device) constructors[0].newInstance();

			device.setBus(bus);
			device.setBaseAddress(Integer.parseInt(deviceBaseAddress, 16));
			device.setAddressSize(Integer.parseInt(deviceAddressSize, 16));
			device.setName(deviceName);

			bus.addDevice(device);
		}

		boolean hasCrtc = (Boolean) props.get("hashCrtc");
		m.hasCrtc = hasCrtc;
		if (hasCrtc) {
			int crtcAddress = Integer.parseInt((String) props.get("crtcAddress"), 16);
			int videoRamAddress = Integer.parseInt((String) props.get("videoRamAddress"), 16);
			int videoRamSize = Integer.parseInt((String) props.get("videoRamSize"), 16);
		}

		return m;
	}

}
