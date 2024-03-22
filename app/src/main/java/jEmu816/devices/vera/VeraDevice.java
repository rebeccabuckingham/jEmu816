package jEmu816.devices.vera;

import jEmu816.Device;

public class VeraDevice extends Device {
	public native boolean videoInit(int window_scale, float screen_x_scale, int mhz);
	public native void videoReset();
	public native void videoEnd();
	public native short videoRead(short reg);
	public native void videoWrite(short reg, short value);
	public native boolean videoStep(int cycles);
	public native boolean videoUpdate();
	public native byte[] getSDLEvent();

	public native void hello();

	static {
		System.loadLibrary("vera");
	}

	public VeraDevice(int baseAddress) {
		super("VERA", baseAddress, 32);
	}

	@Override
	public int getByte(int addr) {
		return videoRead((short) ea(addr));
	}

	@Override
	public void setByte(int addr, int byteValue) {
		videoWrite((short) ea(addr), (short) byteValue);
	}

	public static void main(String[] args) {
		VeraDevice v = new VeraDevice(0xdf00);
		byte[] bytes = v.getSDLEvent();
		System.out.println("bytes[] length is " + bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			System.out.printf("%02x ", bytes[i]);
		}
		System.out.println();
	}

}
