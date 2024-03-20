#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>
#include <jni.h>
#include <SDL.h>
#include "jEmu816_vera_VeraDevice.h"
#include "video.h"

// TODO need to handle this when we call init.
uint8_t machine_mhz = 8;

extern SDL_Event event;

JNIEXPORT jboolean JNICALL 
Java_jEmu816_vera_VeraDevice_videoInit(JNIEnv *env, jobject obj, jint window_scale, jfloat screen_x_scale, jint mhz) {
  machine_mhz = mhz;
  jboolean result = video_init(window_scale, screen_x_scale, "best", 0, 1.0);
  return result;
}

JNIEXPORT void JNICALL 
Java_jEmu816_vera_VeraDevice_videoReset(JNIEnv *env, jobject obj) {
  return video_reset();
}

JNIEXPORT void JNICALL 
Java_jEmu816_vera_VeraDevice_videoEnd(JNIEnv *env, jobject obj) {
  return video_end();
}

JNIEXPORT jshort JNICALL 
Java_jEmu816_vera_VeraDevice_videoRead(JNIEnv *env, jobject obj, jshort reg) {
  return video_read(reg, 0);
}

JNIEXPORT void JNICALL 
Java_jEmu816_vera_VeraDevice_videoWrite(JNIEnv *env, jobject obj, jshort reg, jshort value) {
  video_write(reg, value);
}

JNIEXPORT jboolean JNICALL 
Java_jEmu816_vera_VeraDevice_videoStep(JNIEnv *env, jobject obj, jint cycles) {
  return video_step(machine_mhz, cycles, 0);
}

JNIEXPORT jboolean JNICALL 
Java_jEmu816_vera_VeraDevice_videoUpdate(JNIEnv *env, jobject obj) {
  return video_update();
}

JNIEXPORT void JNICALL 
Java_jEmu816_vera_VeraDevice_hello(JNIEnv *env, jobject obj) {
  puts("fuck, it's wednesday!\n");
}

JNIEXPORT jbyteArray JNICALL 
Java_jEmu816_vera_VeraDevice_getSDLEvent(JNIEnv *env, jobject obj) {
  jbyteArray byteArray = (*env)->NewByteArray(env, sizeof(SDL_Event)); 
  if (byteArray == NULL) {
    return NULL;
  }

  jbyte *bytes = (*env)->GetByteArrayElements(env, byteArray, NULL);
  if (bytes == NULL) {
    return NULL;
  }

  memcpy(bytes, (const void *) &event, sizeof(SDL_Event));
  // for (int i = 0; i < sizeof(SDL_Event); i++) {
  //   bytes[i] = (jbyte) i;
  // }

  (*env)->ReleaseByteArrayElements(env, byteArray, bytes, 0);

  return byteArray;
}