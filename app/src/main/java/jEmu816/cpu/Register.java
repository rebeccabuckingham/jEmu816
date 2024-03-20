package jEmu816.cpu;

public class Register {
	private Cpu cpu;
	private boolean isAccumulator = false;

	public int value = 0;

	private Register(Cpu cpu) {
		this.cpu = cpu;
	}

	public static Register makeAccumulator(Cpu cpu) {
		Register r = new Register(cpu);
		r.isAccumulator = true;
		return r;
	}

	public static Register makeIndexRegister(Cpu cpu) {
		Register r = new Register(cpu);
		r.isAccumulator = false;
		return r;
	}

	public int getWord() {
		return value;
	}

	public int setWord(int value) {
		int wordValue = value & 0xffff;
		this.value = wordValue;
		return wordValue;
	}

	public int getByte() {
		return value & 0xff;
	}

	public int setByte(int value) {
		int byteValue = value & 0xff;
		this.value = byteValue;
		return byteValue;
	}

	public int inc() {
		if (isAccumulator) {
			if (cpu.f.e || cpu.f.m) {
				value++;
				value &= 0xff;
				return getByte();
			} else {
				value++;
				value &= 0xffff;
				return getWord();
			}
		} else {
			if (cpu.f.e || cpu.f.x) {
				value++;
				value &= 0xff;
				return getByte();
			} else {
				value++;
				value &= 0xffff;
				return getWord();
			}
		}
	}

	public int dec() {
		if (isAccumulator) {
			if (cpu.f.e || cpu.f.m) {
				value--;
				if (value == -1) value = 0xff;
				return getByte();
			} else {
				value--;
				if (value == -1) value = 0xffff;
				return getWord();
			}
		} else {
			if (cpu.f.e || cpu.f.x) {
				value--;
				if (value == -1) value = 0xff;
				return getByte();
			} else {
				value--;
				if (value == -1) value = 0xffff;
				return getWord();
			}
		}
	}

	public int getRawValue() {
		return value;
	}

	public void setRawValue(int v) {
		value = v;
	}

	public int getValue() {
		// honors e, m and  x (may need to and with 0xFF in 8-bit mode)
		if (cpu.f.e ||
			(isAccumulator && cpu.f.m) ||
			(!isAccumulator && cpu.f.x)
		) {
			return value & 0xff;
		} else {
			return value;
		}
	}

	public void setValue(int value) {
		// honors the e, m and x flags
		if (cpu.f.e ||
			(isAccumulator && cpu.f.m) ||
			(!isAccumulator && cpu.f.x)
		) {
			this.value = value & 0xff;
		} else {
			this.value = value;
		}
	}
}
