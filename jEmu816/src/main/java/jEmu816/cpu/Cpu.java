package jEmu816.cpu;

import static jEmu816.Util.fullAddressToHex;
import static jEmu816.Util.join;
import static jEmu816.Util.low;
import static jEmu816.Util.swap;
import static jEmu816.Util.toHex;


import jEmu816.ByteSource;
import jEmu816.Disassembler;
import jEmu816.Machine;
import jEmu816.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cpu extends CpuBase implements ByteSource {
	private static final Logger logger = LoggerFactory.getLogger(Cpu.class);

	Disassembler disassembler;

	public Cpu(Machine machine) {
		super(machine);
		this.disassembler = new Disassembler(this);
	}

	@Override
	public int getByte(int addr) {
		return machine.getByte(addr);
	}

	@Override
	public void setByte(int addr, int byteValue) {
		machine.setByte(addr, byteValue);
	}

	public String toString() {
		// emu816 status: pc:00:f000 sp:0100 f:nvMXdIzc:E a:0000 x:0000 y:0000 dp:0000 pcByte: 78 cycles: 0
		int b = machine.getByte(join(pbr, pc));

		// disassemble *next* operation
		String disassembly = disassembler.disassembleInstruction(join(pbr, pc));

		return String.format("pc:%02x:%04x sp:%04x f:%s a:%04x x:%04x y:%04x dp:%04x ",
												 	dbr, pc, sp, f.toString(), a.getWord(), x.getWord(), y.getWord(), dp) +
													disassembly;
	}

	@Override
	public void traceInstruction() {
		System.out.println(toString());
	}

	@Override
	public void dispatch(int opCode) {
		// switch body here
		switch(opCode) {
			case 0x00: op_brk(am_immb()); break;
			case 0x01: op_ora(am_dpix()); break;
			case 0x02: op_cop(am_immb()); break;
			case 0x03: op_ora(am_srel()); break;
			case 0x04: op_tsb(am_dpag()); break;
			case 0x05: op_ora(am_dpag()); break;
			case 0x06: op_asl(am_dpag()); break;
			case 0x07: op_ora(am_dpil()); break;
			case 0x08: op_php(am_impl()); break;
			case 0x09: op_ora(am_immm()); break;
			case 0x0a: op_asla(am_acc()); break;
			case 0x0b: op_phd(am_impl()); break;
			case 0x0c: op_tsb(am_absl()); break;
			case 0x0d: op_ora(am_absl()); break;
			case 0x0e: op_asl(am_absl()); break;
			case 0x0f: op_ora(am_alng()); break;
			case 0x10: op_bpl(am_rela()); break;
			case 0x11: op_ora(am_dpiy()); break;
			case 0x12: op_ora(am_dpgi()); break;
			case 0x13: op_ora(am_sriy()); break;
			case 0x14: op_trb(am_dpag()); break;
			case 0x15: op_ora(am_dpgx()); break;
			case 0x16: op_asl(am_dpgx()); break;
			case 0x17: op_ora(am_dily()); break;
			case 0x18: op_clc(am_impl()); break;
			case 0x19: op_ora(am_absy()); break;
			case 0x1a: op_inca(am_acc()); break;
			case 0x1b: op_tcs(am_impl()); break;
			case 0x1c: op_trb(am_absl()); break;
			case 0x1d: op_ora(am_absx()); break;
			case 0x1e: op_asl(am_absx()); break;
			case 0x1f: op_ora(am_alnx()); break;
			case 0x20: op_jsr(am_absl()); break;
			case 0x21: op_and(am_dpix()); break;
			case 0x22: op_jsl(am_alng()); break;
			case 0x23: op_and(am_srel()); break;
			case 0x24: op_bit(am_dpag()); break;
			case 0x25: op_and(am_dpag()); break;
			case 0x26: op_rol(am_dpag()); break;
			case 0x27: op_and(am_dpil()); break;
			case 0x28: op_plp(am_impl()); break;
			case 0x29: op_and(am_immm()); break;
			case 0x2a: op_rola(am_acc()); break;
			case 0x2b: op_pld(am_impl()); break;
			case 0x2c: op_bit(am_absl()); break;
			case 0x2d: op_and(am_absl()); break;
			case 0x2e: op_rol(am_absl()); break;
			case 0x2f: op_and(am_alng()); break;
			case 0x30: op_bmi(am_rela()); break;
			case 0x31: op_and(am_dpiy()); break;
			case 0x32: op_and(am_dpgi()); break;
			case 0x33: op_and(am_sriy()); break;
			case 0x34: op_bit(am_dpgx()); break;
			case 0x35: op_and(am_dpgx()); break;
			case 0x36: op_rol(am_dpgx()); break;
			case 0x37: op_and(am_dily()); break;
			case 0x38: op_sec(am_impl()); break;
			case 0x39: op_and(am_absy()); break;
			case 0x3a: op_deca(am_acc()); break;
			case 0x3b: op_tsc(am_impl()); break;
			case 0x3c: op_bit(am_absx()); break;
			case 0x3d: op_and(am_absx()); break;
			case 0x3e: op_rol(am_absx()); break;
			case 0x3f: op_and(am_alnx()); break;
			case 0x40: op_rti(am_impl()); break;
			case 0x41: op_eor(am_dpix()); break;
			case 0x42: op_wdm(am_immb()); break;
			case 0x43: op_eor(am_srel()); break;
			case 0x44: op_mvp(am_immw()); break;
			case 0x45: op_eor(am_dpag()); break;
			case 0x46: op_lsr(am_dpag()); break;
			case 0x47: op_eor(am_dpil()); break;
			case 0x48: op_pha(am_impl()); break;
			case 0x49: op_eor(am_immm()); break;
			case 0x4a: op_lsra(am_impl()); break;
			case 0x4b: op_phk(am_impl()); break;
			case 0x4c: op_jmp(am_absl()); break;
			case 0x4d: op_eor(am_absl()); break;
			case 0x4e: op_lsr(am_absl()); break;
			case 0x4f: op_eor(am_alng()); break;
			case 0x50: op_bvc(am_rela()); break;
			case 0x51: op_eor(am_dpiy()); break;
			case 0x52: op_eor(am_dpgi()); break;
			case 0x53: op_eor(am_sriy()); break;
			case 0x54: op_mvn(am_immw()); break;
			case 0x55: op_eor(am_dpgx()); break;
			case 0x56: op_lsr(am_dpgx()); break;
			case 0x57: op_eor(am_dpil()); break;
			case 0x58: op_cli(am_impl()); break;
			case 0x59: op_eor(am_absy()); break;
			case 0x5a: op_phy(am_impl()); break;
			case 0x5b: op_tcd(am_impl()); break;
			case 0x5c: op_jmp(am_alng()); break;
			case 0x5d: op_eor(am_absx()); break;
			case 0x5e: op_lsr(am_absx()); break;
			case 0x5f: op_eor(am_alnx()); break;
			case 0x60: op_rts(am_impl()); break;
			case 0x61: op_adc(am_dpix()); break;
			case 0x62: op_per(am_lrel()); break;
			case 0x63: op_adc(am_srel()); break;
			case 0x64: op_stz(am_dpag()); break;
			case 0x65: op_adc(am_dpag()); break;
			case 0x66: op_ror(am_dpag()); break;
			case 0x67: op_adc(am_dpil()); break;
			case 0x68: op_pla(am_impl()); break;
			case 0x69: op_adc(am_immm()); break;
			case 0x6a: op_rora(am_impl()); break;
			case 0x6b: op_rtl(am_impl()); break;
			case 0x6c: op_jmp(am_absi()); break;
			case 0x6d: op_adc(am_absl()); break;
			case 0x6e: op_ror(am_absl()); break;
			case 0x6f: op_adc(am_alng()); break;
			case 0x70: op_bvs(am_rela()); break;
			case 0x71: op_adc(am_dpiy()); break;
			case 0x72: op_adc(am_dpgi()); break;
			case 0x73: op_adc(am_sriy()); break;
			case 0x74: op_stz(am_dpgx()); break;
			case 0x75: op_adc(am_dpgx()); break;
			case 0x76: op_ror(am_dpgx()); break;
			case 0x77: op_adc(am_dily()); break;
			case 0x78: op_sei(am_impl()); break;
			case 0x79: op_adc(am_absy()); break;
			case 0x7a: op_ply(am_impl()); break;
			case 0x7b: op_tdc(am_impl()); break;
			case 0x7c: op_jmp(am_abxi()); break;
			case 0x7d: op_adc(am_absx()); break;
			case 0x7e: op_ror(am_absx()); break;
			case 0x7f: op_adc(am_alnx()); break;
			case 0x80: op_bra(am_rela()); break;
			case 0x81: op_sta(am_dpix()); break;
			case 0x82: op_brl(am_lrel()); break;
			case 0x83: op_sta(am_srel()); break;
			case 0x84: op_sty(am_dpag()); break;
			case 0x85: op_sta(am_dpag()); break;
			case 0x86: op_stx(am_dpag()); break;
			case 0x87: op_sta(am_dpil()); break;
			case 0x88: op_dey(am_impl()); break;
			case 0x89: op_biti(am_immm()); break;
			case 0x8a: op_txa(am_impl()); break;
			case 0x8b: op_phb(am_impl()); break;
			case 0x8c: op_sty(am_absl()); break;
			case 0x8d: op_sta(am_absl()); break;
			case 0x8e: op_stx(am_absl()); break;
			case 0x8f: op_sta(am_alng()); break;
			case 0x90: op_bcc(am_rela()); break;
			case 0x91: op_sta(am_dpiy()); break;
			case 0x92: op_sta(am_dpgi()); break;
			case 0x93: op_sta(am_sriy()); break;
			case 0x94: op_sty(am_dpgx()); break;
			case 0x95: op_sta(am_dpgx()); break;
			case 0x96: op_stx(am_dpgy()); break;
			case 0x97: op_sta(am_dily()); break;
			case 0x98: op_tya(am_impl()); break;
			case 0x99: op_sta(am_absy()); break;
			case 0x9a: op_txs(am_impl()); break;
			case 0x9b: op_txy(am_impl()); break;
			case 0x9c: op_stz(am_absl()); break;
			case 0x9d: op_sta(am_absx()); break;
			case 0x9e: op_stz(am_absx()); break;
			case 0x9f: op_sta(am_alnx()); break;
			case 0xa0: op_ldy(am_immx()); break;
			case 0xa1: op_lda(am_dpix()); break;
			case 0xa2: op_ldx(am_immx()); break;
			case 0xa3: op_lda(am_srel()); break;
			case 0xa4: op_ldy(am_dpag()); break;
			case 0xa5: op_lda(am_dpag()); break;
			case 0xa6: op_ldx(am_dpag()); break;
			case 0xa7: op_lda(am_dpil()); break;
			case 0xa8: op_tay(am_impl()); break;
			case 0xa9: op_lda(am_immm()); break;
			case 0xaa: op_tax(am_impl()); break;
			case 0xab: op_plb(am_impl()); break;
			case 0xac: op_ldy(am_absl()); break;
			case 0xad: op_lda(am_absl()); break;
			case 0xae: op_ldx(am_absl()); break;
			case 0xaf: op_lda(am_alng()); break;
			case 0xb0: op_bcs(am_rela()); break;
			case 0xb1: op_lda(am_dpiy()); break;
			case 0xb2: op_lda(am_dpgi()); break;
			case 0xb3: op_lda(am_sriy()); break;
			case 0xb4: op_ldy(am_dpgx()); break;
			case 0xb5: op_lda(am_dpgx()); break;
			case 0xb6: op_ldx(am_dpgy()); break;
			case 0xb7: op_lda(am_dily()); break;
			case 0xb8: op_clv(am_impl()); break;
			case 0xb9: op_lda(am_absy()); break;
			case 0xba: op_tsx(am_impl()); break;
			case 0xbb: op_tyx(am_impl()); break;
			case 0xbc: op_ldy(am_absx()); break;
			case 0xbd: op_lda(am_absx()); break;
			case 0xbe: op_ldx(am_absy()); break;
			case 0xbf: op_lda(am_alnx()); break;
			case 0xc0: op_cpy(am_immx()); break;
			case 0xc1: op_cmp(am_dpix()); break;
			case 0xc2: op_rep(am_immb()); break;
			case 0xc3: op_cmp(am_srel()); break;
			case 0xc4: op_cpy(am_dpag()); break;
			case 0xc5: op_cmp(am_dpag()); break;
			case 0xc6: op_dec(am_dpag()); break;
			case 0xc7: op_cmp(am_dpil()); break;
			case 0xc8: op_iny(am_impl()); break;
			case 0xc9: op_cmp(am_immm()); break;
			case 0xca: op_dex(am_impl()); break;
			case 0xcb: op_wai(am_impl()); break;
			case 0xcc: op_cpy(am_absl()); break;
			case 0xcd: op_cmp(am_absl()); break;
			case 0xce: op_dec(am_absl()); break;
			case 0xcf: op_cmp(am_alng()); break;
			case 0xd0: op_bne(am_rela()); break;
			case 0xd1: op_cmp(am_dpiy()); break;
			case 0xd2: op_cmp(am_dpgi()); break;
			case 0xd3: op_cmp(am_sriy()); break;
			case 0xd4: op_pei(am_dpag()); break;
			case 0xd5: op_cmp(am_dpgx()); break;
			case 0xd6: op_dec(am_dpgx()); break;
			case 0xd7: op_cmp(am_dily()); break;
			case 0xd8: op_cld(am_impl()); break;
			case 0xd9: op_cmp(am_absy()); break;
			case 0xda: op_phx(am_impl()); break;
			case 0xdb: op_stp(am_impl()); break;
			case 0xdc: op_jmp(am_abil()); break;
			case 0xdd: op_cmp(am_absx()); break;
			case 0xde: op_dec(am_absx()); break;
			case 0xdf: op_cmp(am_alnx()); break;
			case 0xe0: op_cpx(am_immx()); break;
			case 0xe1: op_sbc(am_dpix()); break;
			case 0xe2: op_sep(am_immb()); break;
			case 0xe3: op_sbc(am_srel()); break;
			case 0xe4: op_cpx(am_dpag()); break;
			case 0xe5: op_sbc(am_dpag()); break;
			case 0xe6: op_inc(am_dpag()); break;
			case 0xe7: op_sbc(am_dpil()); break;
			case 0xe8: op_inx(am_impl()); break;
			case 0xe9: op_sbc(am_immm()); break;
			case 0xea: op_nop(am_impl()); break;
			case 0xeb: op_xba(am_impl()); break;
			case 0xec: op_cpx(am_absl()); break;
			case 0xed: op_sbc(am_absl()); break;
			case 0xee: op_inc(am_absl()); break;
			case 0xef: op_sbc(am_alng()); break;
			case 0xf0: op_beq(am_rela()); break;
			case 0xf1: op_sbc(am_dpiy()); break;
			case 0xf2: op_sbc(am_dpgi()); break;
			case 0xf3: op_sbc(am_sriy()); break;
			case 0xf4: op_pea(am_immw()); break;
			case 0xf5: op_sbc(am_dpgx()); break;
			case 0xf6: op_inc(am_dpgx()); break;
			case 0xf7: op_sbc(am_dily()); break;
			case 0xf8: op_sed(am_impl()); break;
			case 0xf9: op_sbc(am_absy()); break;
			case 0xfa: op_plx(am_impl()); break;
			case 0xfb: op_xce(am_impl()); break;
			case 0xfc: op_jsr(am_abxi()); break;
			case 0xfd: op_sbc(am_absx()); break;
			case 0xfe: op_inc(am_absx()); break;
			case 0xff: op_sbc(am_alnx()); break;
		}

	}

	// operation methods start
	// 0x00|brk|immb|2|7|----di--|+1 if e=0
	private void op_brk(int ea) {
		if (trace) { traceOp("brk"); }
		addCycles(7);

		if (f.e) {
			pushWord(pc);
			pushByte(f.getP() | 0x10);

			f.i = true;
			f.d = false;
			pbr = 0;

			pc = machine.getWord(0xfffe);
			cycles += 7;
		}
		else {
			pushByte(pbr);
			pushWord(pc);
			pushByte(f.getP());

			f.i = true;
			f.d = true;
			pbr = 0;

			pc = machine.getWord(0xffe6);
			cycles += 8;
		}
	}

	// 0x01|ora|dpix|2|6|n-----z-|+1 if m=0, +1 if DP.l ≠ 0
	private void op_ora(int ea) {
		if (trace) { traceOp("ora"); }

		int tmp = 0;

		if (f.e || f.m) {
			tmp = a.getByte() | machine.getByte(ea);
			a.setByte(tmp);
			setnz_b(tmp);
			addCycles(2);
		}
		else {
			tmp = a.getWord() | machine.getWord(ea);
			a.setWord(tmp);
			setnz_w(tmp);
			addCycles(3);
		}

	}

	// 0x02|cop|immb|2|7|----di--|+1 if e=0
	private void op_cop(int ea) {
		if (trace) { traceOp("cop"); }

		if (f.e) {
			pushWord(pc);
			pushByte(f.getP());

			f.i = true;
			f.d = false;
			pbr = 0;

			pc = machine.getWord(0xfff4);
			addCycles(7);
		}
		else {
			pushByte(pbr);
			pushWord(pc);
			pushByte(f.getP());

			f.i = true;
			f.d = true;
			pbr = 0;

			pc = machine.getWord(0xffe4);
			addCycles(8);
		}
	}

	// 0x04|tsb|dpag|2|5|------z-|+2 if m=0, +1 if DP.l ≠ 0
	private void op_tsb(int ea) {
		if (trace) { traceOp("tsb"); }

		if (f.e || f.m) {
			int data = machine.getByte(ea);

			machine.setByte(ea, data | a.getByte());
			setz((a.getByte() & data) == 0);
			addCycles(4);
		}
		else {
			int data = machine.getWord(ea);

			machine.setWord(ea, data | a.getWord());
			setz((a.getWord() & data) == 0);
			addCycles(5);
		}

	}

	// 0x06|asl|dpag|2|5|n-----zc|+2 if m=0, +1 if DP.l ≠ 0
	private void op_asl(int ea) {
		if (trace) { traceOp("asl"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);

			setc(data & 0x80);
			setnz_b(data <<= 1);
			machine.setByte(ea, data);
			cycles += 4;
		}
		else {
			int data = machine.getWord(ea);

			setc(data & 0x8000);
			setnz_w(data <<= 1);
			machine.setWord(ea, data);
			cycles += 5;
		}

	}

	// 0x08|php|impl|1|3|--------
	private void op_php(int ea) {
		if (trace) { traceOp("php"); }
		addCycles(3);
		pushByte(f.getP());
	}

	// 0x0a|asla|acc|1|2|n-----zc
	private void op_asla(int ea) {
		if (trace) { traceOp("asla"); }
		if (f.e || f.m) {
			setc(a.getByte() & 0x80);
			setnz_b(a.setByte(a.getByte() << 1));
			machine.setByte(ea, a.getByte());
		}
		else {
			setc(a.getWord() & 0x8000);
			setnz_w(a.setWord(a.getWord() << 1));
			machine.setWord(ea, a.getWord());
		}
		addCycles(2);
	}

	// 0x0b|phd|impl|1|4|--------
	private void op_phd(int ea) {
		if (trace) { traceOp("phd"); }
		pushWord(dp);
		addCycles(4);
	}

	private void branch(int ea, boolean condition) {
		//logger.debug("branch: ea:" + toHex(ea, 4) + ", condition: " + condition);
		if (condition) {
			if (f.e && ((pc ^ ea) & 0xff00) != 0) ++cycles;
			pc = ea;
			addCycles(3);
		}
		else
			addCycles(2);
	}

	// 0x10|bpl|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_bpl(int ea) {
		if (trace) { traceOp("bpl"); }
		branch(ea, !f.n);
	}

	// 0x14|trb|dpag|2|5|------z-|+2 if m=0, +1 if DP.l ≠ 0
	private void op_trb(int ea) {
		if (trace) { traceOp("trb"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);
			machine.setByte(ea, data | a.getByte());
			setz((a.getByte() & data) == 0);
			addCycles(4);
		}
		else {
			int data = machine.getWord(ea);
			machine.setWord(ea, data | a.getWord());
			setz((a.getWord() & data) == 0);
			addCycles(5);
		}
	}

	// 0x18|clc|impl|1|2|-------c
	private void op_clc(int ea) {
		if (trace) { traceOp("clc"); }
		addCycles(2);
		setc(false);
	}

	// 0x1a|inca|acc|1|2|n-----z-
	private void op_inca(int ea) {
		if (trace) { traceOp("inca"); }
		if (f.e || f.m)
			setnz_b(a.setByte(a.getByte() + 1));
		else
			setnz_w(a.setWord(a.getWord() + 1));

		addCycles(2);
	}

	// 0x1b|tcs|impl|1|2|--------
	private void op_tcs(int ea) {
		if (trace) { traceOp("tcs"); }
		addCycles(2);
		sp = f.e ? (0x0100 | a.getByte()) : a.getWord();
	}

	// 0x20|jsr|absl|3|6|--------
	private void op_jsr(int ea) {
		if (trace) { traceOp("jsr"); }
		pushWord(pc - 1);
		pc = ea;
		addCycles(4);
	}

	// 0x21|and|dpix|2|6|n-----z-|+1 if m=0, +1 if DP.l ≠ 0
	private void op_and(int ea) {
		if (trace) { traceOp("and"); }
		if (f.e || f.m) {
			setnz_b(a.setByte(a.getByte() & machine.getByte(ea)));
			addCycles(2);
		}
		else {
			setnz_w(a.setWord(a.getWord() & machine.getWord(ea)));
			addCycles(3);
		}
	}

	// 0x22|jsl|alng|4|8|--------
	private void op_jsl(int ea) {
		if (trace) { traceOp("jsl"); }
		pushByte(pbr);
		pushWord(pc - 1);

		pbr = low(ea >> 16);
		pc = ea;
		addCycles(5);
	}

	// 0x24|bit|dpag|2|3|--------|+1 if m=0, +1 if DP.l ≠ 0
	private void op_bit(int ea) {
		if (trace) { traceOp("bit"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);

			setz((a.getByte() & data) == 0);
			setn(data & 0x80);
			setv(data & 0x40);
			addCycles(2);
		}
		else {
			int data = machine.getWord(ea);

			setz((a.getWord() & data) == 0);
			setn(data & 0x8000);
			setv(data & 0x4000);
			addCycles(3);
		}
	}

	// 0x26|rol|dpag|2|5|n-----zc|+1 if m=0, +1 if DP.l ≠ 0
	private void op_rol(int ea) {
		if (trace) { traceOp("rol"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);
			int carry = f.c ? 0x01 : 0x00;

			setc(data & 0x80);
			setnz_b(data = (data << 1) | carry);
			machine.setByte(ea, data);
			addCycles(4);
		}
		else {
			int data = machine.getWord(ea);
			int carry = f.c ? 0x0001 : 0x0000;

			setc(data & 0x8000);
			setnz_w(data = (data << 1) | carry);
			machine.setWord(ea, data);
			addCycles(5);
		}
	}

	// 0x28|plp|impl|1|4|--------
	private void op_plp(int ea) {
		if (trace) { traceOp("plp"); }
		addCycles(4);

		if (f.e)
			f.setP(popByte() | 0x30);
		else {
			f.setP(popByte());

			if (f.x) {
				x.setWord(x.getByte());
				y.setWord(y.getByte());
			}
		}
	}

	// 0x2a|rola|acc|1|2|n-----zc
	private void op_rola(int ea) {
		if (trace) { traceOp("rola"); }
		if (f.e || f.m) {
			int carry = f.c ? 0x01 : 0x00;

			setc(a.getByte() & 0x80);
			setnz_b(a.setByte((a.getByte() << 1) | carry));
		}
		else {
			int carry = f.c ? 0x0001 : 0x0000;

			setc(a.getWord() & 0x8000);
			setnz_w(a.setWord((a.getWord()) << 1) | carry);
		}
		addCycles(2);
	}

	// 0x2b|pld|impl|1|5|--------
	private void op_pld(int ea) {
		if (trace) { traceOp("pld"); }
		addCycles(5);
		setnz_w(dp = popWord());
	}

	// 0x30|bmi|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_bmi(int ea) {
		if (trace) { traceOp("bmi"); }
		branch(ea, f.n);
	}

	// 0x38|sec|impl|1|2|-------c
	private void op_sec(int ea) {
		if (trace) { traceOp("sec"); }
		addCycles(2);
		f.c = true;
	}

	// 0x3a|deca|acc|1|2|n-----z-
	private void op_deca(int ea) {
		if (trace) { traceOp("deca"); }
		if (f.e || f.m)
			setnz_b(a.dec());
		else
			setnz_w(a.dec());

		addCycles(2);
	}

	// 0x3b|tsc|impl|1|2|--------
	private void op_tsc(int ea) {
		if (trace) { traceOp("tsc"); }
		addCycles(2);
		if (f.e || f.m)
			setnz_b(low(a.setWord(sp)));
		else
			setnz_w(a.setWord(sp));
	}

	// 0x40|rti|impl|1|6|nvmxdizc|+1 if e=0
	private void op_rti(int ea) {
		if (trace) { traceOp("rti"); }
		if (f.e) {
			f.setP(popByte());
			pc = popWord();
			addCycles(6);
		}
		else {
			f.setP(popByte());
			pc = popWord();
			pbr = popByte();
			addCycles(7);
		}
		f.i = false;
	}

	// 0x41|eor|dpix|2|6|n-----z-|+1 if m=0, +1 if DP.l ≠ 0
	private void op_eor(int ea) {
		if (trace) { traceOp("eor"); }
		if (f.e || f.m) {
			setnz_b(a.setByte(a.getByte() ^ machine.getByte(ea)));
			addCycles(2);
		}
		else {
			setnz_w(a.setWord(a.getWord() ^ machine.getWord(ea)));
			addCycles(3);
		}
	}

	// 0x42|wdm|immb|2|2|--------
	private void op_wdm(int ea) {
		if (trace) { traceOp("wdm"); }
		addCycles(3);
		int sigbyte = machine.getByte(ea);

		logger.warn("**************");
		logger.warn("op_wdm ea: " + fullAddressToHex(ea) + ", sigbyte: " + sigbyte);
		logger.warn("**************");

    if (sigbyte == 0xff) {
      stopped = true;
    }
	}

	// 0x44|mvp|immw|3|*|--------|7 per byte moved|
	// TODO check this one against documentation, I have trouble believing it's right.
	private void op_mvp(int ea) {
		if (trace) { traceOp("mvp"); }

		int src = machine.getByte(ea + 1);
		dbr = machine.getByte(ea);		// dbr is dst

		machine.setByte(join(dbr, y.getWord()), machine.getByte(join(src, x.getWord())));
		y.dec();
		x.dec();
		a.dec();

		if (a.getWord() != 0xffff) pc -= 3;
		addCycles(7);
	}

	// 0x46|lsr|dpag|2|5|n-----zc|+1 if m=0, +1 if DP.l ≠ 0
	private void op_lsr(int ea) {
		if (trace) { traceOp("lsr"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);

			setc(data & 0x01);
			setnz_b(data >>= 1);
			machine.setByte(ea, data);
			addCycles(4);
		}
		else {
			int data = machine.getWord(ea);

			setc(data & 0x0001);
			setnz_w(data >>= 1);
			machine.setWord(ea, data);
			addCycles(5);
		}
	}

	// 0x48|pha|impl|1|3|--------|+1 if m=0
	private void op_pha(int ea) {
		if (trace) { traceOp("pha"); }
		if (f.e || f.m) {
			pushByte(a.getByte());
			addCycles(3);
		}
		else {
			pushWord(a.getWord());
			addCycles(4);
		}
	}

	// 0x4a|lsra|impl|1|2|n-----zc
	private void op_lsra(int ea) {
		if (trace) { traceOp("lsra"); }
		if (f.e || f.m) {
			setc(a.getByte() & 0x01);
			setnz_b(a.setByte(a.getByte() >> 1));
			machine.setByte(ea, a.getByte());
		}	else {
			setc(a.getWord() & 0x0001);
			setnz_w(a.setWord(a.getWord() >> 1));
			machine.setWord(ea, a.getWord());
		}
		addCycles(2);
	}

	// 0x4b|phk|impl|1|3|--------
	private void op_phk(int ea) {
		if (trace) { traceOp("phk"); }
		addCycles(3);
		pushByte(pbr);
	}

	// 0x4c|jmp|absl|3|3|--------
	private void op_jmp(int ea) {
		if (trace) { traceOp("jmp"); }
		pbr = low(ea >> 16);
		pc = ea;
		addCycles(1);
	}

	// 0x50|bvc|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_bvc(int ea) {
		if (trace) { traceOp("bvc"); }
		branch(ea, !f.v);
	}

	// 0x54|mvn|immw|3|*|--------|7 cycles per byte moved
	// TODO check this one against the documentation
	private void op_mvn(int ea) {
		if (trace) { traceOp("mvn"); }
		int src = machine.getByte(ea + 1);
		dbr = machine.getByte(ea);	// dbr is dst

		machine.setByte(join(dbr, y.getWord()), machine.getByte(join(src, x.getWord())));

		y.inc();
		x.inc();

		if (--a.value != 0xffff) pc -= 3;
		addCycles(7);
	}

	// 0x58|cli|impl|1|2|-----i--
	private void op_cli(int ea) {
		if (trace) { traceOp("cli"); }
		addCycles(2);
		f.i = false;
	}

	// 0x5a|phy|impl|1|3|--------|+1 if x=0
	private void op_phy(int ea) {
		if (trace) { traceOp("phy"); }
		if (f.e || f.x) {
			pushByte(y.getByte());
			addCycles(3);
		}
		else {
			pushWord(y.getWord());
			addCycles(4);
		}
	}

	// 0x5b|tcd|impl|1|2|--------
	private void op_tcd(int ea) {
		if (trace) { traceOp("tcd"); }
		addCycles(2);
		dp = a.getWord();
	}

	// 0x60|rts|impl|1|6|--------
	private void op_rts(int ea) {
		if (trace) { traceOp("rts"); }
		addCycles(6);
		pc = (popWord() + 1) & 0xffff;
	}

	// 0x61|adc|dpix|2|6|nv----zc|+1 if m=0, +1 if DP.l ≠ 0
	private void op_adc(int ea) {
		if (trace) { traceOp("adc"); }
		if (f.e || f.m) {
			int	data = machine.getByte(ea);
			int	temp = a.getByte() + data + (f.c ? 1 : 0);

			if (f.d) {
				if ((temp & 0x0f) > 0x09) temp += 0x06;
				if ((temp & 0xf0) > 0x90) temp += 0x60;
			}

			setc(temp & 0x100);
			setv((~(a.getByte() ^ data)) & (a.getByte() ^ temp) & 0x80);
			setnz_b(a.setByte(low(temp)));
			addCycles(2);
		}
		else {
			int data = machine.getWord(ea);
			int	temp = a.getWord() + data + (f.c ? 1 : 0);

			if (f.d) {
				if ((temp & 0x000f) > 0x0009) temp += 0x0006;
				if ((temp & 0x00f0) > 0x0090) temp += 0x0060;
				if ((temp & 0x0f00) > 0x0900) temp += 0x0600;
				if ((temp & 0xf000) > 0x9000) temp += 0x6000;
			}

			setc(temp & 0x10000);
			setv((~(a.getWord() ^ data)) & (a.getWord() ^ temp) & 0x8000);
			setnz_w(a.setWord(temp));
			addCycles(2);
		}
	}

	// 0x62|per|lrel|3|6|--------|
	private void op_per(int ea) {
		if (trace) { traceOp("per"); }
		addCycles(6);
		pushWord(ea);
	}

	// 0x64|stz|dpag|2|3|--------|+1 if m=0, +1 if DP.l ≠ 0
	private void op_stz(int ea) {
		if (trace) { traceOp("stz"); }
		if (f.e || f.m) {
			machine.setByte(ea, 0);
			addCycles(2);
		}
		else {
			machine.setWord(ea, 0);
			addCycles(3);
		}
	}

	// 0x66|ror|dpag|2|5|n-----zc|+1 if m=0, +1 if DP.l ≠ 0
	private void op_ror(int ea) {
		if (trace) { traceOp("ror"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);
			int carry = f.c ? 0x80 : 0x00;

			setc(data & 0x01);
			setnz_b(data = (data >> 1) | carry);
			machine.setByte(ea, data);
			addCycles(4);
		}
		else {
			int data = machine.getWord(ea);
			int carry = f.c ? 0x8000 : 0x0000;

			setc(data & 0x0001);
			setnz_w(data = (data >> 1) | carry);
			machine.setWord(ea, data);
			addCycles(5);
		}
	}

	// 0x68|pla|impl|1|4|--------|+1 if m=0
	private void op_pla(int ea) {
		if (trace) { traceOp("pla"); }
		if (f.e || f.m) {
			setnz_b(a.setByte(popByte()));
			addCycles(4);
		}
		else {
			setnz_w(a.setWord(popWord()));
			addCycles(5);
		}
	}

	// 0x6a|rora|impl|1|2|n-----zc
	private void op_rora(int ea) {
		if (trace) { traceOp("rora"); }
		if (f.e || f.m) {
			int carry = f.c ? 0x80 : 0x00;

			setc(a.getByte() & 0x01);
			setnz_b(a.setByte((a.getByte() >> 1) | carry));
		}
		else {
			int carry = f.c ? 0x8000 : 0x0000;

			setc(a.getWord() & 0x0001);
			setnz_w(a.setWord((a.getWord() >> 1) | carry));
		}
		addCycles(2);
	}

	// 0x6b|rtl|impl|1|6|--------
	private void op_rtl(int ea) {
		if (trace) { traceOp("rtl"); }
		addCycles(6);
		pc = (popWord() + 1) & 0xffff;
		pbr = popByte();
	}

	// 0x70|bvs|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_bvs(int ea) {
		if (trace) { traceOp("bvs"); }
		branch(ea, f.v);
	}

	// 0x78|sei|impl|1|2|-----i--
	private void op_sei(int ea) {
		if (trace) { traceOp("sei"); }
		addCycles(2);
		f.i = true;
	}

	// 0x7a|ply|impl|1|4|--------|+1 if x=0
	private void op_ply(int ea) {
		if (trace) { traceOp("ply"); }
		if (f.e || f.x) {
			setnz_b(low(y.setWord(popByte())));
			addCycles(4);
		}
		else {
			setnz_w(y.setWord(popWord()));
			addCycles(5);
		}
	}

	// 0x7b|tdc|impl|1|2|--------
	private void op_tdc(int ea) {
		if (trace) { traceOp("tdc"); }
		addCycles(2);
		if (f.e || f.m)
			setnz_b(low(a.setWord(dp)));
		else
			setnz_w(a.setWord(dp));
	}

	// 0x80|bra|rela|2|3|--------|+1 if e=1
	private void op_bra(int ea) {
		if (trace) { traceOp("bra"); }
		branch(ea, true);
	}

	// 0x81|sta|dpix|2|6|--------|+1 if m=0, +1 if DP.l ≠ 0
	private void op_sta(int ea) {
		if (trace) { traceOp("sta"); }
		if (f.e || f.m) {
			machine.setByte(ea, a.getByte());
			addCycles(2);
		}
		else {
			machine.setWord(ea, a.getWord());
			addCycles(3);
		}
	}

	// 0x82|brl|lrel|3|4|--------
	private void op_brl(int ea) {
		if (trace) { traceOp("brl"); }
		pc = ea;
		addCycles(3);
	}

	// 0x84|sty|dpag|2|3|--------|+1 if x=0, +1 if DP.l ≠ 0
	private void op_sty(int ea) {
		if (trace) { traceOp("sty"); }
		if (f.e || f.x) {
			machine.setByte(ea, y.getByte());
			addCycles(2);
		}
		else {
			machine.setWord(ea, y.getWord());
			addCycles(3);
		}
	}

	// 0x86|stx|dpag|2|3|--------|+1 if x=0, +1 if DP.l ≠ 0
	private void op_stx(int ea) {
		if (trace) { traceOp("stx"); }
		if (f.e || f.x) {
			machine.setByte(ea, x.getByte());
			addCycles(2);
		}
		else {
			machine.setWord(ea, x.getWord());
			addCycles(3);
		}
	}

	// 0x88|dey|impl|1|2|n-----z-
	private void op_dey(int ea) {
		if (trace) { traceOp("dey"); }
		addCycles(2);
		if (f.e || f.x)
			setnz_b(y.dec());
		else
			setnz_w(y.dec());
	}

	// 0x89|biti|immm|2|2|--------|+1 if m=0
	private void op_biti(int ea) {
		if (trace) { traceOp("biti"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);
			setz((a.getByte() & data) == 0);
		}
		else {
			int data = machine.getWord(ea);
			setz((a.getWord() & data) == 0);
		}
		addCycles(2);
	}

	// 0x8a|txa|impl|1|2|--------
	private void op_txa(int ea) {
		if (trace) { traceOp("txa"); }
		addCycles(2);
		if (f.e || f.m)
			setnz_b(a.setByte(x.getByte()));
		else
			setnz_w(a.setWord(x.getWord()));
	}

	// 0x8b|phb|impl|1|3|--------
	private void op_phb(int ea) {
		if (trace) { traceOp("phb"); }
		addCycles(3);
		pushByte(dbr);
	}

	// 0x90|bcc|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_bcc(int ea) {
		if (trace) { traceOp("bcc"); }
		branch(ea, !f.c);
	}

	// 0x98|tya|impl|1|2|--------
	private void op_tya(int ea) {
		if (trace) { traceOp("tya"); }
		addCycles(2);
		if (f.e || f.m)
			setnz_b(a.setByte(y.getByte()));
		else
			setnz_w(a.setWord(y.getWord()));
	}

	// 0x9a|txs|impl|1|2|--------
	private void op_txs(int ea) {
		if (trace) { traceOp("txs"); }
		if (f.e)
			sp = 0x0100 | x.getByte();
		else
			sp = x.getWord();

		addCycles(2);
	}

	// 0x9b|txy|impl|1|2|--------
	private void op_txy(int ea) {
		if (trace) { traceOp("txy"); }
		addCycles(2);
		if (f.e || f.x)
			setnz_b(low(y.setWord(x.getWord())));
		else
			setnz_w(y.setWord(x.getWord()));
	}

	// 0xa0|ldy|immx|2|2|n-----z-|+1 if x=0
	private void op_ldy(int ea) {
		if (trace) { traceOp("ldy"); }
		if (f.e || f.x) {
			setnz_b(low(y.setByte(machine.getByte(ea))));
			addCycles(2);
		}
		else {
			setnz_w(y.setWord(machine.getWord(ea)));
			addCycles(3);
		}
	}

	// 0xa1|lda|dpix|2|6|n-----z-|+1 if m=0, +1 if DP.l ≠ 0
	private void op_lda(int ea) {
		if (trace) { traceOp("lda"); }
		if (f.e || f.m) {
			setnz_b(low(a.setByte(machine.getByte(ea))));
			addCycles(2);
		}
		else {
			setnz_w(a.setWord(machine.getWord(ea)));
			addCycles(3);
		}
	}

	// 0xa2|ldx|immx|2|2|n-----z-|+1 if x=0
	private void op_ldx(int ea) {
		if (trace) { traceOp("ldx"); }
		if (f.e || f.x) {
			setnz_b(low(x.setByte(machine.getByte(ea))));
			addCycles(2);
		}
		else {
			setnz_w(x.setWord(machine.getWord(ea)));
			addCycles(3);
		}
	}

	// 0xa8|tay|impl|1|2|--------
	private void op_tay(int ea) {
		if (trace) { traceOp("tay"); }
		if (f.e || f.x)
			setnz_b(low(y.setWord(a.getByte())));
		else
			setnz_w(y.setWord(a.getWord()));

		addCycles(2);
	}

	// 0xaa|tax|impl|1|2|--------
	private void op_tax(int ea) {
		if (trace) { traceOp("tax"); }
		if (f.e || f.x)
			setnz_b(low(x.setWord(a.getByte())));
		else
			setnz_w(x.setWord(a.getWord()));

		addCycles(2);
	}

	// 0xab|plb|impl|1|4|--------
	private void op_plb(int ea) {
		if (trace) { traceOp("plb"); }
		addCycles(4);
		setnz_b(dbr = popByte());
	}

	// 0xb0|bcs|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_bcs(int ea) {
		if (trace) { traceOp("bcs"); }
		branch(ea, f.c);
	}

	// 0xb8|clv|impl|1|2|-v--00--
	private void op_clv(int ea) {
		if (trace) { traceOp("clv"); }
		addCycles(2);
		f.v = false;
	}

	// 0xba|tsx|impl|1|2|--------
	private void op_tsx(int ea) {
		if (trace) { traceOp("tsx");	}
		if (f.e)
			setnz_b(x.setByte(sp & 0xff));
		else
			setnz_w(x.setWord(sp));

		addCycles(2);
	}

	// 0xbb|tyx|impl|1|2|--------
	private void op_tyx(int ea) {
		if (trace) { traceOp("tyx"); }
		if (f.e || f.x)
			setnz_b(low(x.setWord(y.getWord())));
		else
			setnz_w(x.setWord(y.getWord()));

		addCycles(2);
	}

	// 0xc0|cpy|immx|2|2|n-----zc|+1 if x=0
	private void op_cpy(int ea) {
		if (trace) { traceOp("cpy"); }
		if (f.e || f.x) {
			int	data = machine.getByte(ea);
			int temp = y.getByte() - data;

			setc(temp & 0x100);
			setnz_b(low(temp));
			addCycles(2);
		}
		else {
			int data = machine.getWord(ea);
			int	temp = y.getWord() - data;

			setc(temp & 0x10000);
			setnz_w(temp);
			addCycles(3);
		}
	}

	// 0xc1|cmp|dpix|2|6|n-----zc|+1 if m=0, +1 if DP.l ≠ 0
	private void op_cmp(int ea) {
		if (trace) { traceOp("cmp"); }
		if (f.e || f.m) {
			int	data = machine.getByte(ea);
			int	temp = a.getByte() - data;

			setc(temp & 0x100);
			setnz_b(low(temp));
			addCycles(2);
		}
		else {
			int data = machine.getWord(ea);
			int temp = a.getWord() - data;

			setc((temp & 0x10000L) != 0);
			setnz_w(temp);
			addCycles(3);
		}
	}

	// 0xc2|rep|immb|2|3|nvmxdizc
	private void op_rep(int ea) {
		if (trace) { traceOp("rep"); }
		addCycles(3);
		f.setP(f.getP() & ~machine.getByte(ea));
		if (f.e) f.m = f.x = true;
	}

	// 0xc6|dec|dpag|2|5|n-----z-|+2 if m=0, +1 if DP.l ≠ 0
	private void op_dec(int ea) {
		if (trace) { traceOp("dec"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);
			machine.setByte(ea, --data);
			setnz_b(data);
			addCycles(4);
		} else {
			int data = machine.getWord(ea);
			machine.setWord(ea, --data);
			setnz_w(data);
			addCycles(5);
		}
	}

	// 0xc8|iny|impl|1|2|n-----z-
	private void op_iny(int ea) {
		if (trace) { traceOp("iny"); }
		addCycles(2);
		y.inc();
		if (f.e || f.x) {
			setnz_b(y.getByte());
		} else {
			setnz_w(y.getWord());
		}
	}

	// 0xca|dex|impl|1|2|n-----z-
	private void op_dex(int ea) {
		if (trace) { traceOp("dex"); }
		addCycles(2);
		x.dec();
		if (f.e || f.x)
			setnz_b(x.getByte());
		else
			setnz_w(x.getWord());
	}

	// 0xcb|wai|impl|1|3|--------|additional cycles needed by interrupt handler to restart the processor
	private void op_wai(int ea) {
		if (trace) { traceOp("wai"); }
		if (!interrupted) {
			pc -= 1;
		} else
			interrupted = false;

		addCycles(3);
	}

	// 0xd0|bne|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_bne(int ea) {
		if (trace) { traceOp("bne"); }
		branch(ea, !f.z);
	}

	// 0xd4|pei|dpag|2|6|--------|+1 if DP.l ≠ 0
	private void op_pei(int ea) {
		if (trace) { traceOp("pei"); }
		addCycles(6);
		pushWord(machine.getWord(ea));
	}

	// 0xd8|cld|impl|1|2|----d---
	private void op_cld(int ea) {
		if (trace) { traceOp("cld"); }
		addCycles(2);
		f.d = false;
	}

	// 0xda|phx|impl|1|3|--------|+1 if x=0
	private void op_phx(int ea) {
		if (trace) { traceOp("phx"); }
		if (f.e || f.x) {
			pushByte(y.getByte());
			addCycles(3);
		}
		else {
			pushWord(y.getWord());
			addCycles(4);
		}
	}

	// 0xdb|stp|impl|1|3|--------
	private void op_stp(int ea) {
		if (trace) { traceOp("stp"); }
		if (!interrupted) {
			pc -= 1;
		}
		else
			interrupted = false;
		addCycles(3);
	}

	// 0xe0|cpx|immx|2|2|n-----zc|+1 if x=0
	private void op_cpx(int ea) {
		if (trace) { traceOp("cpx"); }
		if (f.e || f.x) {
			int	data = machine.getByte(ea);
			int temp = x.getByte() - data;

			setc(temp & 0x100);
			setnz_b(low(temp));
			addCycles(2);
		}
		else {
			int data = machine.getWord(ea);
			int	temp = x.getWord() - data;

			setc(temp & 0x10000);
			setnz_w(temp);
			addCycles(3);
		}
	}

	// 0xe1|sbc|dpix|2|6|nv----zc|+1 if m=0, +1 if DP.l ≠ 0
	private void op_sbc(int ea) {
		if (trace) { traceOp("sbc"); }
		if (f.e || f.m) {
			int data = ~machine.getByte(ea);
			int	temp = a.getByte() + data + (f.c ? 1 : 0);

			if (f.d) {
				if ((temp & 0x0f) > 0x09) temp += 0x06;
				if ((temp & 0xf0) > 0x90) temp += 0x60;
			}

			setc(temp & 0x100);
			setv((~(a.getByte() ^ data)) & (a.getByte() ^ temp) & 0x80);
			setnz_b(a.setByte(low(temp)));
			addCycles(2);
		}
		else {
			int data = ~machine.getWord(ea);
			int	temp = a.getWord() + data + (f.c ? 1 : 0);

			if (f.d) {
				if ((temp & 0x000f) > 0x0009) temp += 0x0006;
				if ((temp & 0x00f0) > 0x0090) temp += 0x0060;
				if ((temp & 0x0f00) > 0x0900) temp += 0x0600;
				if ((temp & 0xf000) > 0x9000) temp += 0x6000;
			}

			setc(temp & 0x10000);
			setv((~(a.getWord() ^ data)) & (a.getWord() ^ temp) & 0x8000);
			setnz_w(a.setWord(temp));
			addCycles(3);
		}
	}

	// 0xe2|sep|immb|2|3|nvmxdizc
	private void op_sep(int ea) {
		if (trace) { traceOp("sep"); }
		f.setP(f.getP() | machine.getByte(ea));
		if (f.e) f.m = f.x = true;

		if (f.x) {
			x.setWord(x.getByte());
			y.setWord(y.getByte());
		}
		addCycles(3);
	}

	// 0xe6|inc|dpag|2|5|n-----z-|+2 if m=0, +1 if DP.l ≠ 0
	private void op_inc(int ea) {
		if (trace) { traceOp("inc"); }
		if (f.e || f.m) {
			int data = machine.getByte(ea);
			machine.setByte(ea, ++data);
			setnz_b(data);
			addCycles(4);
		}
		else {
			int data = machine.getWord(ea);
			machine.setWord(ea, ++data);
			setnz_w(data);
			addCycles(5);
		}
	}

	// 0xe8|inx|impl|1|2|n-----z-
	private void op_inx(int ea) {
		if (trace) { traceOp("inx"); }
		addCycles(2);
		x.inc();
		if (f.e || f.x) {
			setnz_b(x.getByte());
		} else {
			setnz_w(x.getWord());
		}
	}

	// 0xea|nop|impl|1|2|--------
	private void op_nop(int ea) {
		if (trace) { traceOp("nop"); }
		addCycles(2);
	}

	// 0xeb|xba|impl|1|3|n-----z-
	private void op_xba(int ea) {
		if (trace) { traceOp("xba"); }
		a.setWord(swap(a.getWord()));
		setnz_b(a.getByte());
		addCycles(3);
	}

	// 0xf0|beq|rela|2|2|--------|+1 if branch taken, +1 if e=1
	private void op_beq(int ea) {
		if (trace) { traceOp("beq"); }
		branch(ea, f.z);
	}

	// 0xf4|pea|immw|3|5|--------|check
	private void op_pea(int ea) {
		if (trace) { traceOp("pea"); }
		pushWord(machine.getWord(ea));
		addCycles(5);
	}

	// 0xf8|sed|impl|1|2|----d---
	private void op_sed(int ea) {
		if (trace) { traceOp("sed"); }
		f.d = true;
		addCycles(2);
	}

	// 0xfa|plx|impl|1|4|--------|+1 if x=0
	private void op_plx(int ea) {
		if (trace) { traceOp("plx"); }
		if (f.e || f.x) {
			setnz_b(low(x.setWord(popByte())));
			addCycles(4);
		}
		else {
			setnz_w(x.setWord(popWord()));
			addCycles(5);
		}
	}

	// 0xfb|xce|impl|1|2|--mx---c : e
	private void op_xce(int ea) {
		if (trace) { traceOp("xce"); }
		boolean oe = f.e;

		f.e = f.c;
		f.c = oe;

		if (f.e) {
			f.setP(f.getP() | 0x30);
			sp = (0x0100 | (sp & 0xff));
		}
		addCycles(2);
	}
}
