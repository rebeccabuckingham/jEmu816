package jEmu816.cpu;

import static jEmu816.Util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jEmu816.Machine;

public abstract class CpuBase {
	private static final Logger logger = LoggerFactory.getLogger(CpuBase.class);

	private static final int NMI_VECTOR_6502 = 0xfffa;
	private static final int RESET_VECTOR_6502 = 0xfffc;
	private static final int BRK_VECTOR_6502 = 0xfffe;

	public Flags f = new Flags();
	public int pc;
	public int pbr, dbr, sp, dp;

	public boolean stopped = true;
	public boolean irq = false;
	public boolean trace = false;
	public boolean interrupted = false;

	public Machine machine;

	public Register a;
	public Register x;
	public Register y;

	public int cycles = 0;
	public int bytes = 0;

	public CpuBase(Machine machine) {
		this.machine = machine;
		machine.cycles = 0;

		a = Register.makeAccumulator(this);
		x = Register.makeIndexRegister(this);
		y = Register.makeIndexRegister(this);
		dp = 0;
	}

	public String toString() {
		return
			"pc:" + fullAddressToHex(pc) +
				" sp:" + toHex(sp, 4) +
				" f:" + f.toString() +
				" a:" + toHex(a.getValue(), 4) +
				" x:" + toHex(x.getValue(), 4) +
				" y:" + toHex(y.getValue(), 4) +
				" dp:" + toHex(dp, 4) +
				" dbr:" + fullAddressToHex(dbr << 16) +
				" pcByte: " + toHex(machine.getByte(join(pbr, pc)), 2);
	}

	private void decSP() {
		sp--;
		if (sp == -1) {
			sp = 0xffff;
		}
	}

	private void incSP() {
		sp++;
		if (sp == 0x10000) {
			sp = 0x0000;
		}
	}

	public void pushByte(int value) {
		machine.setByte(sp, value);
		decSP();
	}

	public void pushWord(int value) {
		pushByte(high(value));
		pushByte(low(value));
	}

	public int popByte() {
		incSP();
		return machine.getByte(sp);
	}

	public int popWord() {
		int l = popByte();
		int h = popByte();
		return join16(l, h);
	}

	protected int getWordAtPC() {
		return machine.getWord(join(pbr, pc));
	}

	protected int getByteAtPC() {
		return machine.getByte(join(pbr, pc));
	}

	public void reset() {
		pc = machine.getWord(RESET_VECTOR_6502);
		sp = 0x00ff;
		pbr = 0x00;
		dbr = 0x00;
		stopped = true;
		irq = false;
	 	machine.cycles = 0;
		f.reset();
		a.setValue(0);
		x.setValue(0);
		y.setValue(0);
		dp = 0;
	}

	int fetchOpcode() {
		int b = getByteAtPC();
		pc = (pc + 1) & 0xffff;
		return b;
	}

	protected void addBytes(int bytes) {
		this.bytes += bytes;
	}

	protected void addCycles(int cycles) {
		this.cycles += cycles;
	}

	private String addrModeStr;
	private String operationStr;

	protected void traceAm(String addressingMode) {
		if (trace) {
			addrModeStr = addressingMode;
		}
	}

	protected void traceOp(String operation) {
		if (trace) {
			operationStr = operation;
		}
	}

	private void traceInstruction() {
		// note: might need to snapshot the registers at the beginning of step().
		logger.debug("--something goes here--");
	}

	public abstract void dispatch(int opCode);

	public void step() {
		// will make calls to things that will need to be marked abstract in this class.
		cycles = 0;
		bytes = 0;
		long wallTime = System.nanoTime();
		int opCode = fetchOpcode();
		dispatch(opCode);

		if (trace) {
			traceInstruction();
		}

		machine.cycles += cycles;
		pc = (pc + bytes) & 0xffff;
	}

  // stopped is set to true when WDM is executed with a $ff signature byte.
	public void loop() {
    stopped = false;
    while (!stopped) {
      step();
    }
	}

	protected int getAddr(int ea) {
		return join(machine.getByte(ea + 2), machine.getWord(ea));
	}

	public void setn(int flag) {
		f.n = (flag != 0);
	}

	public void setv(int flag) {
		f.v = (flag != 0);
	}

	public void setd(int flag) {
		f.d = (flag != 0);
	}

	public void seti(int flag) {
		f.i = (flag != 0);
	}

	public void setz(boolean flag) {
		f.z = flag;
	}

	public void setc(boolean flag) {
		f.c = flag;
	}

	public void setc(int a) {
		f.c = a != 0;
	}

	public void setnz_b(int byteValue) {
		setn(byteValue & 0x80);
		f.z = (byteValue == 0);
	}

	public void setnz_w(int wordValue) {
		setn(wordValue & 0x8000);
		f.z = (wordValue == 0);
	}

	// Absolute - a
	public int am_absl() {
		if (trace) { traceAm("am_absl"); }
		int ea = join(dbr, getWordAtPC());
		addBytes(2);
		addCycles(2);
		return ea;
	}

	// addressing mode methods start
	// Absolute Indexed X - a,X
	public int am_absx() {
		if (trace) { traceAm("am_absx"); }
		int ea = join(dbr, getWordAtPC()) + x.getWord();
		addBytes(2);
		addCycles(2);
		return ea;
	}

	// Absolute Indexed Y - a,Y
	public int am_absy() {
		if (trace) { traceAm("am_absy"); }
		int ea = join(dbr, getWordAtPC()) + y.getWord();
		addBytes(2);
		addCycles(2);
		return ea;
	}

	// Absolute Indirect - (a)
	public int am_absi() {
		if (trace) { traceAm("am_absi"); }
		int ia = join(0, machine.getWord(bank(pbr) | pc));
		addBytes(2);
		addCycles(4);
		return (join(0, machine.getWord(ia)));
	}

	// Absolute Indexed Indirect - (a,X)
	public int am_abxi() {
		if (trace) { traceAm("am_abxi"); }
		int ia = join(0, machine.getWord(bank(pbr) | pc)) + x.getWord();
		addBytes(2);
		addCycles(4);
		return (join(0, machine.getWord(ia)));
	}

	// Absolute Long - >a
	public int am_alng() {
		if (trace) { traceAm("am_alng"); }
		int ea = getAddr(join(pbr, pc));
		addBytes(3);
		addCycles(3);
		return ea;
	}

	// Absolute Long Indexed - >a,X
	public int am_alnx() {
		if (trace) { traceAm("am_alnx"); }
		int ea = getAddr(join(pbr, pc)) + x.getWord();
		addBytes(3);
		addCycles(3);
		return ea;
	}

	// Absolute Indirect Long - [a]
	public int am_abil() {
		if (trace) { traceAm("am_abil"); }
		int ia = bank(0) | machine.getWord(join(pbr, pc));

		addBytes(2);
		addCycles(5);
		return (getAddr(ia));
	}

	// Direct Page - d
	public int am_dpag() {
		if (trace) { traceAm("am_dpag"); }
		int offset = machine.getByte(bank(pbr) | pc);
		addBytes(1);
		addCycles(1);
		return (bank(0) | (dp + offset));
	}

	// Direct Page Indexed X - d,X
	public int am_dpgx() {
		if (trace) { traceAm("am_dpgx"); }
		int offset = machine.getByte(bank(pbr) | pc) + x.getByte();
		addBytes(1);
		addCycles(1);
		return (bank(0) | (dp + offset));
	}

	// Direct Page Indexed Y - d,Y
	public int am_dpgy() {
		if (trace) { traceAm("am_dpgy"); }
		int offset = machine.getByte(bank(pbr) | pc) + y.getByte();
		addBytes(1);
		addCycles(1);
		return (bank(0) | (dp + offset));
	}

	// Direct Page Indirect - (d)
	public int am_dpgi() {
		if (trace) { traceAm("am_dpgi"); }
		int disp = machine.getByte(bank(pbr) | pc);
		addBytes(1);
		addCycles(3);
		return (bank(dbr) | machine.getWord(bank(0) | (dp + disp)));
	}

	// Direct Page Indexed Indirect - (d,x)
	public int am_dpix() {
		if (trace) { traceAm("am_dpix"); }
		int disp = machine.getByte(bank(pbr) | pc);
		addBytes(1);
		addCycles(3);
		return (bank(dbr) | machine.getWord(bank(0) | (dp + disp + x.getWord())));
	}

	// Direct Page Indirect Indexed - (d),Y
	public int am_dpiy() {
		if (trace) { traceAm("am_dpiy"); }
		int disp = machine.getByte(bank(pbr) | pc);
		addBytes(1);
		addCycles(3);
		return (bank(dbr) | machine.getWord(bank(0) | (dp + disp + y.getWord())));
	}

	// Direct Page Indirect Long - [d]
	public int am_dpil() {
		if (trace) { traceAm("am_dpil"); }
		int disp = machine.getByte(bank(pbr) | pc);
		addBytes(1);
		addCycles(4);
		return (getAddr(bank(0) | (dp + disp)));
	}

	// Direct Page Indirect Long Indexed - [d],Y
	public int am_dily() {
		if (trace) { traceAm("am_dily"); }
		int disp = machine.getByte(bank(pbr) | pc);
		addBytes(1);
		addCycles(4);
		return (getAddr(bank(0) | (dp + disp)) + y.getWord());
	}

	// Implied/Stack
	public int am_impl() {
		if (trace) { traceAm("am_impl"); }
		return 0;
	}

	// Accumulator
	public int am_acc() {
		if (trace) { traceAm("am_acc"); }
		return 0;
	}

	// Immediate Byte
	public int am_immb() {
		if (trace) { traceAm("am_immb"); }
		int ea = bank(pbr) | pc;
		addBytes(1);
		return ea;
	}

	// Immediate Word
	public int am_immw() {
		if (trace) { traceAm("am_immw"); }
		int ea = bank(pbr) | pc;
		addBytes(2);
		addCycles(1);
		return ea;
	}

	// Immediate based on size of A/M
	public int am_immm() {
		if (trace) { traceAm("am_immm"); }
		int ea = join(pbr, pc);
		int size = (f.e || f.m) ? 1 : 2;
		addBytes(size);
		addCycles(size - 1);
		return ea;
	}

	// Immediate based on size of X/Y
	public int am_immx() {
		if (trace) { traceAm("am_immx"); }
		int ea = join(pbr, pc);
		int size = (f.e || f.x) ? 1 : 2;
		addBytes(size);
		addCycles(size - 1);
		return ea;
	}

	// Long Relative - d
	public int am_lrel() {
		if (trace) { traceAm("am_lrel"); }
		int disp = machine.getWord(join(pbr, pc));
		addBytes(2);
		addCycles(2);
		return (bank(pbr) | (pc + disp));
	}

	// Relative - d
	public int am_rela() {
		if (trace) { traceAm("am_rela"); }
		int disp = machine.getByte(join(pbr, pc));
		addBytes(1);
		addCycles(1);
		return (bank(pbr) | (pc + disp));
	}

	// Stack Relative - d,S
	public int am_srel() {
		if (trace) { traceAm("am_srel"); }
		int disp = machine.getByte(join(pbr, pc));
		addBytes(1);
		addCycles(1);
		if (f.e)
			return((bank(0) | join(low(sp) + disp, high(sp))));
		else
			return (bank(0) | (sp + disp));
	}

	// Stack Relative Indirect Indexed Y - (d,S),Y
	public int am_sriy() {
		if (trace) { traceAm("am_sriy"); }
		int disp = machine.getByte(join(pbr, pc));
		int ia = 0;
		addBytes(1);
		addCycles(3);
		if (f.e)
			ia = machine.getWord(join(low(sp) + disp, high(sp)));
		else
			ia = machine.getWord(bank(0) | (sp + disp));

		return (bank(dbr) | (ia + y.getWord()));
	}

	// addressing mode methods end

}
