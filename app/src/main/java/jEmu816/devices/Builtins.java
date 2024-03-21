package jEmu816.devices;

import jEmu816.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jEmu816.cpu.Constants265;

public class Builtins extends Device {
  private static final Logger logger = LoggerFactory.getLogger(Builtins.class);

  private Uart[] uarts;
  private int uartBase = 0xdf70;


  public Builtins() {
    super("65265Bi", 0xdf00, 192);

    uarts = new Uart[] {
      new Uart(0),
      new Uart(1),
      new Uart(2),
      new Uart(3)
    };

  }

  private Uart getUartFromAddr(int addr) {
    if (addr >= Constants265.ACSR0 && addr <= Constants265.ARTD0)
      return uarts[0];
    else if (addr >= Constants265.ACSR1 && addr <= Constants265.ARTD1)
      return uarts[1];
    else if (addr >= Constants265.ACSR2 && addr <= Constants265.ARTD2)
      return uarts[2];
    else if (addr >= Constants265.ACSR3 && addr <= Constants265.ARTD3)
      return uarts[3];

    return null;
  }

  @Override
  public int getByte(int addr) {
    if (addr >= Constants265.ACSR0 && addr < Constants265.PIBFR) {
      return getUartFromAddr(addr).getByte(addr);
    }
    return 0;
  }

  @Override
  public void setByte(int addr, int byteValue) {
    if (addr >= Constants265.ACSR0 && addr < Constants265.PIBFR) {
      getUartFromAddr(addr).setByte(addr, byteValue);
    } else {

    }
  }

  public static class Uart {
    int portNum;
    public int baseAddress;

    private int ea(int addr) {
      return addr - baseAddress;
    }

    public Uart(int portNum) {
      this.portNum = portNum;
      this.baseAddress = Constants265.ACSR0 + (2 * portNum);
    }

    public int getByte(int addr) {
      int ea = ea(addr);
      return 0;
    }

    public void setByte(int addr, int byteValue) {
      int ea = ea(addr);
    }
  }
}
