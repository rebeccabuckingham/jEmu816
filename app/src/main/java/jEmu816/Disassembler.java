package jEmu816;

import jEmu816.cpu.Cpu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jEmu816.Util.*;

public class Disassembler extends DisassemblerBase {
	private static final Logger logger = LoggerFactory.getLogger(Disassembler.class);

	Cpu cpu;

	public Disassembler(Cpu cpu) {
		this.cpu = cpu;
	}

	public String disassembleInstruction(int address) {
		var tmp = toHex(address, 6);
		String addrDisp = tmp.substring(0,2) + ":" + tmp.substring(2);

		int op = cpu.getByte(address++);

		String byteDisp = toHex(op, 2) + " ";

		AM addrMode = addressModes[op];
		String mnemonic = opcodeNames[op];
		String operand = "";

		int b1, b2, b3, disp, target;

		switch(addrMode) {
		case ABSI:	// (abs) -> ($1234)
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			operand = "($" + toHex(join16(b1, b2), 4) + ")";
			break;
		case ABSL:	// abs -> $1234
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			operand = "$" + toHex(join16(b1, b2), 4);
			break;
		case ABSX:	// abs,x -> $1234,x
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			operand = "$" + toHex(join16(b1, b2), 4) + ",x";
			break;
		case ABSY:	// abs,y -> $1234,y
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			operand = "$" + toHex(join16(b1, b2), 4) + ",y";
			break;
		case ABXI:	// (abs,x) -> ($1234,x)
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			operand = "($" + toHex(join16(b1, b2), 4) + ",x)";
			break;
		case ACC:
			// does nothing
			break;
		case ALNG:	// long -> $123456
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			b3 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			byteDisp += " ";
			byteDisp += toHex(b3, 3);
			operand = "$" + toHex(join(b3, join16(b1, b2)), 6);
			break;
		case ALNX:	// long,x -> $123456,x
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			b3 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			byteDisp += " ";
			byteDisp += toHex(b3, 3);
			operand = "$" + toHex(join(b3, join16(b1, b2)), 6) + ",x";
			break;
		case DILY:	// [dir],y -> [$00],y
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "[$" + toHex(b1, 2) + "],y";
			break;
		case DPAG:	// dir -> $00
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "$" + toHex(b1, 2);
			break;
		case DPGI:	// (dir) -> ($00)
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "($" + toHex(b1, 2) + ")";
			break;
		case DPGX:	// dir,x -> $00,x
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "$" + toHex(b1, 2) + ",x";
			break;
		case DPGY:	// dir,y -> $00,y
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "$" + toHex(b1, 2) + ",y";
			break;
		case DPIL:	// [dir] -> [$00]
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "[$" + toHex(b1, 2) + "]";
			break;
		case DPIX:	// (dir,x) -> ($00,x)
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "($" + toHex(b1, 2) + ",x)";
			break;
		case DPIY:	// (dir),y -> ($00),y
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "($" + toHex(b1, 2) + "),y";
			break;
		case IMMB:
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "#$" + toHex(b1, 2);
			break;
		case IMMM:
			operand = "#$";
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			if (!cpu.f.e && !cpu.f.m) {
				b2 = cpu.getByte(address++);
				byteDisp += " ";
				byteDisp += toHex(b2, 2);
				operand += toHex(join16(b1, b2), 4);
			} else
				operand += toHex(b1, 2);
			break;
		case IMMW:
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			operand = "#$" + toHex(join16(b1, b2), 2);
			break;
		case IMMX:
			operand = "#$";
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			if (!cpu.f.e && !cpu.f.x) {
				b2 = cpu.getByte(address++);
				byteDisp += " ";
				byteDisp += toHex(b2, 2);
				operand += toHex(join16(b1, b2), 4);
			} else
				operand += toHex(b1, 2);
			break;
		case IMPL:
			// does nothing
			break;
		case LREL:	// rel16 -> $1234 [displacement]
			// todo make sure to check this one against some examples
			// branches disp bytes away from address of next instruction (wraps)
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			disp = join16(b1, b2);
			if (disp > 32767) {
				disp = 65536 - disp;
			}
			target = (address + disp) & 0xffff;
			operand += toHex(target, 4);
			break;
		case MODE:
			break;
		case RELA:
			// todo make sure to check this one against some examples
			// branches disp bytes away from address of next instruction
			disp = cpu.getByte(address++);
			byteDisp += toHex(disp, 2);
			if (disp > 127) {
				disp = 256 - disp;
			}
			target = (address + disp) & 0xffff;
			operand += toHex(target, 4);
			break;
		case SREL:	// stk,S	-> $00,s
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "$" + toHex(b1, 2) + ",s";
			break;
		case SRIY:	// (stk,S),Y -> ($00,s),y
			b1 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			operand = "($" + toHex(b1, 2) + ",s),y";
			break;
		case ABIL:	// [abs] -> [$1234]
			b1 = cpu.getByte(address++);
			b2 = cpu.getByte(address++);
			byteDisp += toHex(b1, 2);
			byteDisp += " ";
			byteDisp += toHex(b2, 2);
			operand = "[$" + toHex(join16(b1, b2), 4) + "]";
			break;
		}

		//     00:1234 01 02 03 04 ora blah
		String paddedByteDisp = (byteDisp + "            ").substring(0, 12);
		return addrDisp + " " + paddedByteDisp + mnemonic + " " + operand;
		// return addrDisp + " " + byteDisp + mnemonic + " " + operand;
	}


}
