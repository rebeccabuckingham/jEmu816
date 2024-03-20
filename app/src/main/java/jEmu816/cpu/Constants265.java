package jEmu816.cpu;

public class Constants265 {
  // emulation mode IRQ vectors
  public static final int IRQAT3_EMU_VECTOR_65265 = 0xFFEE; // UART3 Transmitter Interrupt
  public static final int IRQAR3_EMU_VECTOR_65265 = 0xFFEC; // UART3 Receiver Interrupt
  public static final int IRQAT2_EMU_VECTOR_65265 = 0xFFEA; // UART2 Transmitter Interrupt
  public static final int IRQAR2_EMU_VECTOR_65265 = 0xFFE8; // UART2 Receiver Interrupt
  public static final int IRQAT1_EMU_VECTOR_65265 = 0xFFE6; // UART1 Transmitter Interrupt
  public static final int IRQAR1_EMU_VECTOR_65265 = 0xFFE4; // UART1 Receiver Interrupt
  public static final int IRQAT0_EMU_VECTOR_65265 = 0xFFE2; // UART0 Transmitter Interrupt
  public static final int IRQAR0_EMU_VECTOR_65265 = 0xFFE0; // UART0 Receiver Interrupt
  public static final int IRQ_EMU_VECTOR_65265 = 0xFFDE;    // IRQ Level Interrupt
  public static final int IRQPIB_EMU_VECTOR_65265 = 0xFFDC; // Parallel Interface Bus (PIB) Interrupt
  public static final int IRNE66_EMU_VECTOR_65265 = 0xFFDA; // Negative Edge Interrupt on P66
  public static final int IRNE64_EMU_VECTOR_65265 = 0xFFD8; // Negative Edge Interrupt on P64
  public static final int IRPE62_EMU_VECTOR_65265 = 0xFFD6; // Positive Edge Interrupt on P62 for PWM
  public static final int IRPE60_EMU_VECTOR_65265 = 0xFFD4; // Positive Edge Interrupt on P60
  public static final int IRNE57_EMU_VECTOR_65265 = 0xFFD2; // Negative Edge Interrupt on P57
  public static final int IRPE56_EMU_VECTOR_65265 = 0xFFD0; // Positive Edge Interrupt on P56
  public static final int IRQT7_EMU_VECTOR_65265 = 0xFFCE;  // Timer 7 Interrupt
  public static final int IRQT6_EMU_VECTOR_65265 = 0xFFCC;  // Timer 6 Interrupt
  public static final int IRQT5_EMU_VECTOR_65265 = 0xFFCA;  // Timer 5 Interrupt
  public static final int IRQT4_EMU_VECTOR_65265 = 0xFFC8;  // Timer 4 Interrupt
  public static final int IRQT3_EMU_VECTOR_65265 = 0xFFC6;  // Timer 3 Interrupt
  public static final int IRQT2_EMU_VECTOR_65265 = 0xFFC4;  // Timer 2 Interrupt
  public static final int IRQT1_EMU_VECTOR_65265 = 0xFFC2;  // Timer 1 Interrupt
  public static final int IRQT0_EMU_VECTOR_65265 = 0xFFC0;  // Timer 0 Interrupt

  // native mode IRQ vectors
  public static final int NMI_VECTOR_65265 = 0xFFBA;        // Non-Maskable Interrupt
  public static final int ABORT_VECTOR_65265 = 0xFFB8;      // ABORT Interrupt
  public static final int BRK_VECTOR_65265 = 0xFFB6;        // BRK Software Interrupt
  public static final int COP_VECTOR_65265 = 0xFFB4;        // COP Software Interrupt
  public static final int IRQAT3_VECTOR_65265 = 0xFFAE;     // UART3 Transmitter Interrupt
  public static final int IRQAR3_VECTOR_65265 = 0xFFAC;     // UART3 Receiver Interrupt
  public static final int IRQAT2_VECTOR_65265 = 0xFFAA;     // UART2 Transmitter Interrupt
  public static final int IRQAR2_VECTOR_65265 = 0xFFA8;     // UART2 Receiver Interrupt
  public static final int IRQAT1_VECTOR_65265 = 0xFFA6;     // UART1 Transmitter Interrupt
  public static final int IRQAR1_VECTOR_65265 = 0xFFA4;     // UART1 Receiver Interrupt
  public static final int IRQAT0_VECTOR_65265 = 0xFFA2;     // UART0 Transmitter Interrupt
  public static final int IRQAR0_VECTOR_65265 = 0xFFA0;     // UART0 Receiver Interrupt
  public static final int IRQ_VECTOR_65265 = 0xFF9E;        // IRQ Level Interrupt
  public static final int IRQPIB_VECTOR_65265 = 0xFF9C;     // Parallel Interface Bus (PIB) Interrupt
  public static final int IRNE66_VECTOR_65265 = 0xFF9A;     // Negative Edge Interrupt on P66
  public static final int IRNE64_VECTOR_65265 = 0xFF98;     // Negative Edge Interrupt on P64
  public static final int IRPE62_VECTOR_65265 = 0xFF96;     // Positive Edge Interrupt on P62 for
  public static final int IRPE60_VECTOR_65265 = 0xFF94;     // Positive Edge Interrupt on P60
  public static final int IRNE57_VECTOR_65265 = 0xFF92;     // Negative Edge Interrupt on P57
  public static final int IRPE56_VECTOR_65265 = 0xFF90;     // Positive Edge Interrupt on P56
  public static final int IRQT7_VECTOR_65265 = 0xFF8E;      // Timer 7 Interrupt
  public static final int IRQT6_VECTOR_65265 = 0xFF8C;      // Timer 6 Interrupt
  public static final int IRQT5_VECTOR_65265 = 0xFF8A;      // Timer 5 Interrupt
  public static final int IRQT4_VECTOR_65265 = 0xFF88;      // Timer 4 Interrupt
  public static final int IRQT3_VECTOR_65265 = 0xFF86;      // Timer 3 Interrupt
  public static final int IRQT2_VECTOR_65265 = 0xFF84;      // Timer 2 Interrupt
  public static final int IRQT1_VECTOR_65265 = 0xFF82;      // Timer 1 Interrupt
  public static final int IRQT0_VECTOR_65265 = 0xFF80;      // Timer 0 Interrupt

  // hardware registers
  public static final int PD0 = 0x00DF00;   // Port 0 Data Register
  public static final int PD1 = 0x00DF01;   // Port 1 Data Register
  public static final int PD2 = 0x00DF02;   // Port 2 Data Register
  public static final int PD3 = 0x00DF03;   // Port 3 Data Register
  public static final int PDD0 = 0x00DF04;  // Port 0 Data Direction Register
  public static final int PDD1 = 0x00DF05;  // Port 1 Data Direction Register
  public static final int PDD2 = 0x00DF06;  // Port 2 Data Direction Register
  public static final int PDD3 = 0x00DF07;  // Port 3 Data Direction Register
  public static final int PD4 = 0x00DF20;   // Port 4 Data Register
  public static final int PD5 = 0x00DF21;   // Port 5 Data Register
  public static final int PD6 = 0x00DF22;   // Port 6 Data Register
  public static final int PD7 = 0x00DF23;   // Port 7 Data Register
  public static final int PDD4 = 0x00DF24;  // Port 4 Data Direction Register
  public static final int PDD5 = 0x00DF25;  // Port 5 Data Direction Register
  public static final int PDD6 = 0x00DF26;  // Port 6 Data Direction Register
  public static final int PCS7 = 0x00DF27;  // Port 7 Chip Select
  public static final int BCR = 0x00DF40;   // Bus Control Register
  public static final int SSCR = 0x00DF41;  // System Speed Control Register
  public static final int TCR = 0x00DF42;   // Timer Control Register
  public static final int TER = 0x00DF43;   // Timer Enable Register
  public static final int TIFR = 0x00DF44;  // Timer Interrupt Flag Register
  public static final int EIFR = 0x00DF45;  // Edge Interrupt Flag Register
  public static final int TIER = 0x00DF46;  // Timer Interrupt Enable Register
  public static final int EIER = 0x00DF47;  // Edge Interrupt Enable Register
  public static final int UIFR = 0x00DF48;  // UART Interrupt Flag Register
  public static final int UIER = 0x00DF49;  // UART Interrupt Enable Register
  public static final int T0LL = 0x00DF50;  // Timer 0 Latch Low
  public static final int T0LH = 0x00DF51;  // Timer 0 Latch High
  public static final int T1LL = 0x00DF52;  // Timer 1 Latch Low
  public static final int T1LH = 0x00DF53;  // Timer 1 Latch High
  public static final int T2LL = 0x00DF54;  // Timer 2 Latch Low
  public static final int T2LH = 0x00DF55;  // Timer 2 Latch High
  public static final int T3LL = 0x00DF56;  // Timer 3 Latch Low
  public static final int T3LH = 0x00DF57;  // Timer 3 Latch High
  public static final int T4LL = 0x00DF58;  // Timer 4 Latch Low
  public static final int T4LH = 0x00DF59;  // Timer 4 Latch High
  public static final int T5LL = 0x00DF5A;  // Timer 5 Latch Low
  public static final int T5LH = 0x00DF5B;  // Timer 5 Latch High
  public static final int T6LL = 0x00DF5C;  // Timer 6 Latch Low
  public static final int T6LH = 0x00DF5D;  //  Timer 6 Latch High
  public static final int T7LL = 0x00DF5E;  //  Timer 7 Latch Low
  public static final int T7LH = 0x00DF5F;  // Timer 7 Latch High
  public static final int T0CL = 0x00DF60;  // Timer 0 Counter Low
  public static final int T0CH = 0x00DF61;  // Timer 0 Counter High
  public static final int T1CL = 0x00DF62;  // Timer 1 Counter Low
  public static final int T1CH = 0x00DF63;  // Timer 1 Counter High
  public static final int T2CL = 0x00DF64;  // Timer 2 Counter Low
  public static final int T2CH = 0x00DF65;  // Timer 2 Counter High
  public static final int T3CL = 0x00DF66;  // Timer 3 Counter Low
  public static final int T3CH = 0x00DF67;  // Timer 3 Counter High
  public static final int T4CL = 0x00DF68;  // Timer 4 Counter Low
  public static final int T4CH = 0x00DF69;  // Timer 4 Counter High
  public static final int T5CL = 0x00DF6A;  // Timer 5 Counter Low
  public static final int T5CH = 0x00DF6B;  // Timer 5 Counter High
  public static final int T6CL = 0x00DF6C;  // Timer 6 Counter Low
  public static final int T6CH = 0x00DF6D;  // Timer 6 Counter High
  public static final int T7CL = 0x00DF6E;  // Timer 7 Counter Low
  public static final int T7CH = 0x00DF6F;  // Timer 7 Counter High
  public static final int ACSR0 = 0x00DF70; // UART 0 Control/Status Register
  public static final int ARTD0 = 0x00DF71; // UART 0 Data Register
  public static final int ACSR1 = 0x00DF72; // UART 1 Control/Status Register
  public static final int ARTD1 = 0x00DF73; // UART 1 Data Register
  public static final int ACSR2 = 0x00DF74; // UART 2 Control/Status Register
  public static final int ARTD2 = 0x00DF75; // UART 2 Data Register
  public static final int ACSR3 = 0x00DF76; // UART 3 Control/Status Register
  public static final int ARTD3 = 0x00DF77; // UART 3 Data Register
  public static final int PIBFR = 0x00DF78; // Parallel Interface Flag Register
  public static final int PIBER = 0x00DF79; // Parallel Interface Enable Register
  public static final int PIR2 = 0x00DF7A;  // Parallel Interface Register 2
  public static final int PIR3 = 0x00DF7B;  // Parallel Interface Register 3
  public static final int PIR4 = 0x00DF7C;  // Parallel Interface Register 4
  public static final int PIR5 = 0x00DF7D;  // Parallel Interface Register 5
  public static final int PIR6 = 0x00DF7E;  // Parallel Interface Register 6
  public static final int PIR7 = 0x00DF7F;  // Parallel Interface Register 7
}
