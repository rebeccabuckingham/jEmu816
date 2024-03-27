# jEmu816
65816 System Emulator.  Features 6545 CRTC and Vera emulation.

The Vera Emulation uses SDL2 and a C-based JNI library.

## Installation
Installable releases are in the works, but for now, you can build on your own machine:

### macOS and Linux:

1) Install Java.  You need JDK 17 or newer.
2) Install SDL2.  For macOS, it's best to build SDL2
from source.  This will put the SDL headers and libraries into /usr/local.
Otherwise, you end up with a Framework which Wontwork.

### Windows:

Not currently supported.  It *should* work, but I don't have a Windows machine, so I can't make sure the Makefile for
the Vera emulation is correct.  If you get it to work, let me know how and I'll add support here.  (No Visual Studio Solution files, please.)

## Running

Gradle can build and run jEmu816 from the command line. This includes building the C-based VERA emulation.</br>
On macOS, make sure you set JAVA_HOME first, like so:

```
export JAVA_HOME=`/usr/libexec/java_home`
```

### macOS
If using Vera emulation:</br>
```
./gradlew run -PuseVera --args='/arguments/'</br>
```
If not using Vera emulation (Crtc/headless machine types.)</br>
```
./gradlew run --args='/arguments/'></br>
```
### Linux
Vera emulation just works on Linux, so always use:
```
./gradlew run --args='/arguments/'
```

### Example
```
./gradlew run --args='-m jEmu816.machines.CrtcMachine -s 8 -f ./examples/echoTest-xa.prg'
```

### Arguments
| Option                        | Description |
|-------------------------------| --- |
| -h, --help                    | Shows a help message and exits. |
| -m MACHINE, --machine MACHINE | Specifies the machine to emulate.  Default is jEmu816.machines.CrtcMachine.|
| -s SPEED, --speed SPEED | Specifies emulated speed in Mhz (default: 8 |
|  -f FILENAME, --filename FILENAME | Specifies a binary file to load and optionally run. |
|  -w {true,false}, --wait {true,false} | Do  not  run  automatically,  wait  for  debugger.  (default: false) |
|  -p PORT, --port PORT | Port to  use  for  debugging  interface  (default: 6502) |
| -a STARTADDRESS, --startAddress STARTADDRESS | Sets the start address.  Use 0xnnnnn for hexadecimal. |

### Machine Types
| Machine Name | Description |
|--- | --- |
| jEmu816.machines.CrtcMachine | A machine that uses the 6545 Crtc display controller and provides a 80x25 display. |
| JEmu816.machines.Sentinel | An emulation of the Sentinel65x machine currently in development.|


### Debugger
  jEmu816 has a built-in debugger that is accessed via telnet-ing to the port specified above.  This is generally port 6502.

To connect to the debugger:
```
  telnet localhost 6502
```
