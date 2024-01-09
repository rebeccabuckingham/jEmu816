package jEmu816.cpu;

public class Flags {

	public boolean n;
	public boolean v;
	public boolean m = true;
	public boolean x = true;
	public boolean d;
	public boolean i = true;
	public boolean z;
	public boolean c;

	public boolean e = true; 		// 65C816 starts in emulated mode

	public int getP() {					// never includes e
		int value = 0;

		if (n) value += 0x80;
		if (v) value += 0x40;
		if (m) value += 0x20;
		if (x) value += 0x10;
		if (d) value += 0x08;
		if (i) value += 0x04;
		if (z) value += 0x02;
		if (c) value += 0x01;

		return value;
	}

	public void setP(int value) {
		n = (value & 0x80) == 0x80;
		v = (value & 0x40) == 0x40;
		m = (value & 0x20) == 0x20;
		x = (value & 0x10) == 0x10;
		d = (value & 0x08) == 0x08;
		i = (value & 0x04) == 0x04;
		z = (value & 0x02) == 0x02;
		c = (value & 0x01) == 0x01;
	}

	public String toString() {
		return String.valueOf(n ? 'N' : 'n') +
			(v ? 'V' : 'v') +
			(m ? 'M' : 'm') +
			(x ? 'X' : 'x') +
			(d ? 'D' : 'd') +
			(i ? 'I' : 'i') +
			(z ? 'Z' : 'z') +
			(c ? 'C' : 'c') + '-' +
			(e ? 'E' : 'e');
	}

	public void reset() {
		e = true;
		setP(0x20 | 0x10 | 0x04);
	}
}
