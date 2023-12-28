#include <jEmu816_Main.h>

JNIEXPORT jint JNICALL Java_jEmu816_Main_intMethod
  (JNIEnv *env, jobject obj, jint num) {
    return num * num;
  }