#include <jEmu816_Main.h>

#define	RAM_SIZE	(512 * 1024)
#define MEM_MASK	(512 * 1024L - 1)

// just tests that the library indeed works
JNIEXPORT jint JNICALL Java_jEmu816_Main_intMethod
  (JNIEnv *env, jobject obj, jint num) {
    return num * num;
  }

// initializes the emu816 memory
JNIEXPORT void JNICALL Java_jEmu816_Main_init
  (JNIEnv *env, jobject obj) {
    emu816::setMemory(MEM_MASK, RAM_SIZE, NULL);
  }

// calls emu816::step() and returns a status string
JNIEXPORT jstring JNICALL Java_jEmu816_Main_step
  (JNIEnv *env, jobject obj) {
    return NULL;
  }

// loads an S28 file into memory
JNIEXPORT void JNICALL Java_jEmu816_Main_loadFile
  (JNIEnv *env, jobject obj, jstring filename) {

  }

// resets the emulator
JNIEXPORT void JNICALL Java_jEmu816_Main_reset
  (JNIEnv *env, jobject obj) {

  }
