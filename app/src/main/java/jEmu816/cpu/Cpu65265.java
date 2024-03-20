package jEmu816.cpu;

import jEmu816.Builtins;
import jEmu816.Bus;
import jEmu816.Machine;

import static jEmu816.cpu.Constants265.*;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cpu65265 extends Cpu {
  private static final Logger logger = LoggerFactory.getLogger(Cpu65265.class);

  ExtraIRQ assertedIrq = null;

  public Cpu65265(Machine machine, Bus b, int nanosPerCycle) {
    super(machine, b, nanosPerCycle);
    
    // these guys always come along for the ride.
    Builtins builtins = new Builtins();
    b.addDevice(builtins);
  }
  
  public void checkAndHandleExtraIRQ() {
    if (assertedIrq != null) {
      int vector = (f.e) ? assertedIrq.emuVector : assertedIrq.vector;

      assertedIrq = null;

      if (f.e) {
        pushWord(pc);
        pushByte(f.getP());
  
        f.i = true;
        f.d = false;
        pbr = 0;
  
        pc = bus.getWord(vector) & 0xffff;
        addCycles(7);
      } else {
        pushByte(pbr);
        pushWord(pc);
        pushByte(f.getP());
  
        f.i = true;
        f.d = false;
        pbr = 0;
  
        pc = bus.getWord(vector) & 0xffff;
        addCycles(8);
      }      
    }
  }

  public void assertIrq(ExtraIRQ type) {
    assertedIrq = type;
  }

  // this is so we can trigger the "right" IRQ for 65265 built-in features
  public enum ExtraIRQ {
    DEFAULT(IRQ_EMU_VECTOR_65265, IRQ_VECTOR_65265),   // the standard IRQ
    UART3_RX(IRQAR3_EMU_VECTOR_65265, IRQAR3_VECTOR_65265),
    UART3_TX(IRQAT3_EMU_VECTOR_65265, IRQAT3_VECTOR_65265);

    public final int vector;
    public final int emuVector;
    private ExtraIRQ(int emuVector, int vector) {
      this.emuVector = emuVector;
      this.vector = vector;
    }
  }

}
