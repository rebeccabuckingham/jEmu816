package jEmu816;

import jEmu816.cpu.Constants265;

// emulate one of the 65c265's built-in UARTs
//
// not going to work with how Devices work.  Need to basically sketch out 
// all of the built-in hardware.
//
public class Uart265 extends Device {
  private static final int CONTROL_REG = 0;
  private static final int DATA_REG = 1;

  public Uart265(int portNum) {
    super("UART#" + portNum, Constants265.ACSR0 + (portNum * 2), 2);
  }

  // TODO: these should look at the right bits in UIER
  public boolean getTxIrqEnabled() { return false; }
  public boolean getRxIrqEnabled() { return false; }
  
  // TODO: set the right bits in UIFR, assert the IRQ
  public void assertTxIrq() { }
  public void assertRxIrq() { }

  @Override
  public int getByte(int addr) {
    int realAddr = addr - baseAddress;
   
    if (realAddr == CONTROL_REG) {

    } else {

    }

    return 0;
  }

  @Override
  public void setByte(int addr, int byteValue) {
    int realAddr = addr - baseAddress;
    
    if (realAddr == CONTROL_REG) {

    } else {

    }

  }

  // ******************** external interface methods **************************
  // call this in RunLoop to send a byte from the console to the UART
  // so, from the console's keyboard to make available for a 65816 program.
  public void sendToUart(int byteVal) {

  }

  // call this in RunLoop to get the waiting to send byte from the UART 
  // so, from the 65816 program to send to the console.
  public int getFromUart() {
    return 0;
  }
  // **************************************************************************

  
}
