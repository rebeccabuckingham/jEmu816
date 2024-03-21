package jEmu816;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jEmu816.cpu.Cpu;
import jEmu816.devices.Ram;
import jEmu816.machines.TestMachine;
import static jEmu816.Util.*;
import static spark.Spark.post;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.HashMap;

public class Tester {
	private static final Logger logger = LoggerFactory.getLogger(Tester.class);

	Machine m;
	Cpu cpu;
	ObjectMapper om;

	int pbr, pc;

	public Tester() {
		m = new TestMachine();
		cpu = m.getBus().getCpu();
		om = new ObjectMapper();
	}

	void setFlagsFromStr(String str) {
		int p = 0;
		for (int i = 0; i <= 7; i++) {
			String c = str.substring(i, i+1);
			if (c.equals(c.toUpperCase())) {
				p += (int) Math.pow(2, 7 - i);
			}
		}
		cpu.f.setP(p);
		cpu.f.e = str.endsWith("E");
	}

	void setRegisters(String str) {
		String[] pairs = str.split(" ");

		pc = 0;
		pbr = 0;

		for (String pair : pairs) {
			String[] kv = pair.split(":");
			switch (kv[0]) {
			case "pbr":
				pbr = Integer.parseInt(kv[1], 16);
				cpu.pbr = pbr;
				break;
			case "pc":
				pc = Integer.parseInt(kv[1], 16);
				cpu.pc = pc;
				break;
			case "sp":
				cpu.sp = Integer.parseInt(kv[1], 16);
				break;
			case "f":
				setFlagsFromStr(kv[1]);
				break;
			case "a":
				cpu.a.setRawValue(Integer.parseInt(kv[1], 16));
				break;
			case "x":
				cpu.x.setRawValue(Integer.parseInt(kv[1], 16));
				break;
			case "y":
				cpu.y.setRawValue(Integer.parseInt(kv[1], 16));
				break;
			case "dp":
				cpu.dp = Integer.parseInt(kv[1],16);
				break;
			case "dbr":
				cpu.dbr = Integer.parseInt(kv[1],16);
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> setUpTest(HashMap<String, Object> initial) {
		cpu.cycles = 0;
		setRegisters((String) initial.get("status"));

		// poke code
		int addr = join(pbr, pc);
		ArrayList<String> code = (ArrayList<String>) initial.get("code");
		for (int i = 0; i < code.size(); i++) {
			String _byte = code.get(i);
			cpu.setByte(addr + i, Integer.parseInt(_byte, 16));
		}

		// poke ram
		ArrayList<ArrayList<String>> ram = (ArrayList<ArrayList<String>>) initial.get("ram");
		if (!ram.isEmpty()) {
			for (int i = 0; i < ram.size(); i++) {
				ArrayList<String> pair = ram.get(i);
				cpu.setByte(Integer.parseInt(pair.get(0), 16), Integer.parseInt(pair.get(1), 16));
			}
		}

		return ram;
	}

	void fillMemory() {
		Device ram = m.getDevices().get(0);

		for (int i = 0; i < ram.getAddressSize(); i++) {
			if ((i % 4) == 0) ram.setByte(i, 0xde);
			if ((i % 4) == 1) ram.setByte(i, 0xad);
			if ((i % 4) == 2) ram.setByte(i, 0xbe);
			if ((i % 4) == 3) ram.setByte(i, 0xef);
		}
	}

	void zeroMemory() {
		Ram ram = (Ram) m.getDevices().get(0);
		ram.fillMemory(0);
	}

	@SuppressWarnings("unchecked")
	public String runTest(String body) throws JsonProcessingException {
		HashMap<String,Object> props = new ObjectMapper().readValue(body, HashMap.class);
		String name = (String) props.get("name");

		HashMap<String, Object> initial = (HashMap<String, Object>) props.get("initial");
		zeroMemory();
		setUpTest(initial);

		logger.info("before step: " + cpu.toString());
		cpu.step();
		String rawStatus = cpu.toString();
		logger.info(" after step: " + rawStatus);

		var _cycles = rawStatus.indexOf("cycles:");
		var _spaceAfter = rawStatus.indexOf(' ', _cycles);
		String status = rawStatus.substring(0, _spaceAfter);

		HashMap<String, Object> actual = new HashMap<>();
		actual.put("status", status);

		// actual.ram
		ArrayList<ArrayList<String>> ram = new ArrayList<>();
		ArrayList<ArrayList<String>> initialRam = (ArrayList<ArrayList<String>>) initial.get("ram");
		if (!initialRam.isEmpty()) {
			initialRam.forEach(entry -> {
				int addr = Integer.parseInt(entry.get(0), 16);
				int value = cpu.getByte(addr);
				var _entry = new ArrayList<String>();
				_entry.add(entry.get(0));
				_entry.add(Integer.toString(value, 16));
				ram.add(_entry);
			});
		}

		actual.put("ram", ram);
		props.put("actual", actual);

		return om.writeValueAsString(props);
	}

	public static void main(String[] args) {
		Tester tester = new Tester();

		post("/test", ((request, response) -> {
			response.status(200);
			response.type("application/json");
			return tester.runTest(request.body());
		}));
	}
}
