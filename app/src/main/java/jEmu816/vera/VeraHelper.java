package jEmu816.vera;

public class VeraHelper {
	public static final int AUTO_NONE = 0x000000;
	public static final int AUTO_INC_1 = 0x100000;
	public static final int AUTO_INC_2 = 0x200000;
	public static final int AUTO_INC_4 = 0x300000;
	public static final int AUTO_INC_8 = 0x400000;
	public static final int AUTO_INC_16 = 0x500000;
	public static final int AUTO_INC_32 = 0x600000;
	public static final int AUTO_INC_64 = 0x700000;
	public static final int AUTO_INC_128 = 0x800000;
	public static final int AUTO_INC_256 = 0x900000;
	public static final int AUTO_INC_512 = 0xA00000;
	public static final int AUTO_INC_40 = 0xB00000;
	public static final int AUTO_INC_80 = 0xC00000;
	public static final int AUTO_INC_160 = 0xD00000;
	public static final int AUTO_INC_320 = 0xE00000;
	public static final int AUTO_INC_640 = 0xF00000;
	public static final int DISABLED = 0;
	public static final int ENABLED = 1;
	public static final int VERA_L_BPP1 = 0b00000000;
	public static final int VERA_L_BPP2 = 0b00000001;
	public static final int VERA_L_BPP4 = 0b00000010;
	public static final int VERA_L_BPP8 = 0b00000011;
	public static final int VERA_L_BITMAP = 0b00000100;
	public static final int VERA_L_T256C = 0b00001000;
	public static final int VERA_L_32W = 0b00000000;
	public static final int VERA_L_64W = 0b00010000;
	public static final int VERA_L_128W = 0b00100000;
	public static final int VERA_L_256W = 0b00110000;
	public static final int VERA_L_32H = 0b00000000;
	public static final int VERA_L_64H = 0b01000000;
	public static final int VERA_L_128H = 0b10000000;
	public static final int VERA_L_256H = 0b11000000;
	public static final int VERA_PSG_BASE = 0x1F9C0;
	public static final int VERA_PALETTE_BASE = 0x1FA00;
	public static final int VERA_SPRITES_BASE = 0x1FC00;
	public static final int VERA_TILESIZE8x8 = 0b00000000;
	public static final int VERA_TILESIZE16x8 = 0b00000001;
	public static final int VERA_TILESIZE8x16 = 0b00000010;
	public static final int VERA_TILESIZE16x16 = 0b00000011;

	public static final short REG_ADDR_L = 0x00;
	public static final short REG_ADDR_M = 0x01;
	public static final short REG_ADDR_H = 0x02;
	public static final short REG_DATA_0 = 0x03;
	public static final short REG_DC_HSCALE = 0x0a;
	public static final short REG_DC_VSCALE = 0x0b;
	public static final short REG_DC_VIDEO = 0x09;

	public static final short REG_L0_CONFIG = 0x0d;
	public static final short REG_L0_MAPBASE = 0x0e;
	public static final short REG_L0_TILEBASE = 0x0f;
	public static final short REG_L0_HSCROLL_L = 0x10;
	public static final short REG_L0_HSCROLL_H = 0x11;
	public static final short REG_L0_VSCROLL_L = 0x12;
	public static final short REG_L0_VSCROLL_H = 0x13;

	public static final short REG_L1_CONFIG = 0x14;
	public static final short REG_L1_MAPBASE = 0x15;
	public static final short REG_L1_TILEBASE = 0x16;
	public static final short REG_L1_HSCROLL_L = 0x17;
	public static final short REG_L1_HSCROLL_H = 0x18;
	public static final short REG_L1_VSCROLL_L = 0x19;
	public static final short REG_L1_VSCROLL_H = 0x1a;

	public static void VSET(VeraDevice d, int value) {
		int low = value & 0xff;
		int mid = (value >> 8) & 0xff;
		int high = (value >> 16) & 0xff;

		d.videoWrite(REG_ADDR_L, (short) low);
		d.videoWrite(REG_ADDR_M, (short) mid);
		d.videoWrite(REG_ADDR_H, (short) high);
	}

	public static int VERA_MAPBASE(int value) {
		return value >> 9;
	}

	public static int VERA_TILEBASE(int value, int size) {
		return ((value >> 9) & 0b11111100) | size;
	}

}
