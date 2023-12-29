#include <string.h>
#include "emu816.h"
#include <jEmu816_Main.h>

#define	RAM_SIZE	(512 * 1024)
#define MEM_MASK	(512 * 1024L - 1)

// just tests that the library indeed works
JNIEXPORT jint JNICALL Java_jEmu816_Main_intMethod
  (JNIEnv *env, jobject obj, jint num) {
    return 20 + (num * num);
  }

// initializes the emu816 memory
JNIEXPORT void JNICALL Java_jEmu816_Main_init
  (JNIEnv *env, jobject obj) {
    emu816::setMemory(MEM_MASK, RAM_SIZE, NULL);
  }

// calls emu816::step() and returns a status string
JNIEXPORT jstring JNICALL Java_jEmu816_Main_step
  (JNIEnv *env, jobject obj) {
    char buffer[128];
    strcpy(buffer, "");

    emu816::step();

    // TODO: copy status stuff into buffer
    // pc:00:f001 sp:00ff f:nvmxdIzcE a:0000 x:0000 y:0000 dp:0000 dbr:00:0000 pcByte: d8 cycles: 12
    // 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
    //           1         2         3         4         5         6         7         8         9
    

    jstring javaString = env->NewStringUTF(buffer);

    return javaString;
  }

// loads an S28 file into memory
JNIEXPORT void JNICALL Java_jEmu816_Main_loadFile
  (JNIEnv *env, jobject obj, jstring filename) {

  }

// resets the emulator
JNIEXPORT void JNICALL Java_jEmu816_Main_reset
  (JNIEnv *env, jobject obj) {

  }
