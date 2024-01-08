#include <string.h>
#include <memory.h>
#include <iostream>
#include <fstream>
#include <string>
#include "emu816.h"
#include "jEmu816_NativeWrapper.h"

using namespace std;

#define	RAM_SIZE	(0x1000000)
#define MEM_MASK	(0x1000000 - 1)

void load(const char *filename);

typedef unsigned short Word;
typedef unsigned char Byte;

// just tests that the library indeed works
JNIEXPORT jint JNICALL Java_jEmu816_NativeWrapper_isStopped
  (JNIEnv *, jobject) {
    return emu816::isStopped();
  }

// initializes the emu816 memory
JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_init
  (JNIEnv *env, jobject obj) {
    puts("emu816 wrapper init");
    emu816::setMemory(MEM_MASK, RAM_SIZE, NULL);
  }

// calls emu816::step() and returns a status string
JNIEXPORT jstring JNICALL Java_jEmu816_NativeWrapper_step
  (JNIEnv *env, jobject obj) {
    char *buffer = (char *) malloc(128);
    strcpy(buffer, "");

    emu816::step();
    emu816::getStatus(buffer);

    jstring javaString = env->NewStringUTF(buffer);

    free(buffer);

    return javaString;
  }

JNIEXPORT jstring JNICALL Java_jEmu816_NativeWrapper_getStatus
  (JNIEnv *env, jobject obj) {
    char *buffer = (char *) malloc(128);
    strcpy(buffer, "");
    emu816::getStatus(buffer);

    jstring javaString = env->NewStringUTF(buffer);

    free(buffer);

    return javaString;    
  }  

// loads an S28 file into memory
JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_loadFile
  (JNIEnv *env, jobject obj, jstring filename) {
    const char* str = env->GetStringUTFChars(filename, 0);
    printf("emu816 loadFile filename is '%s'\n", str);
    load(str);
    env->ReleaseStringUTFChars(filename, str);    
  }

// resets the emulator
JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_reset
  (JNIEnv *env, jobject obj) {
    emu816::reset(0);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setByte
  (JNIEnv *env, jobject obj, jint addr, jint value) {
    emu816::setByte(addr, (Byte) value & 0xff);
  }

JNIEXPORT jint JNICALL Java_jEmu816_NativeWrapper_getByte
  (JNIEnv *env, jobject obj, jint addr) {
    return emu816::getByte(addr);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_resetCycles
  (JNIEnv *, jobject) {
    emu816::cycles = 0;
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setP
  (JNIEnv *, jobject, jint value) {
    emu816::p.b = (Byte) (value & 0xff);
  }

JNIEXPORT jint JNICALL Java_jEmu816_NativeWrapper_getP
  (JNIEnv *, jobject) {
    return (jint) emu816::p.b; 
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setE
  (JNIEnv *, jobject, jint value) {
    emu816::e = (Byte) (value != 0 ? 1 : 0);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setPBR
  (JNIEnv *, jobject, jint v) {
    emu816::pbr = (Byte) (v & 0xff);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setPC
  (JNIEnv *, jobject, jint v) {
    emu816::pc = (Word) (v & 0xffff);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setSP
  (JNIEnv *, jobject, jint v) {
    emu816::sp.w = (Word) (v & 0xffff);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setDBR
  (JNIEnv *, jobject, jint v) {
    emu816::dbr = (Byte) (v & 0xff);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setDP
  (JNIEnv *, jobject, jint v) {
    emu816::dp.w = (Word) (v & 0xffff);
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setA
  (JNIEnv *, jobject, jint v) {
    emu816::a.w = v;
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setX
  (JNIEnv *, jobject, jint v) {
    emu816::x.w = v;
  }

JNIEXPORT void JNICALL Java_jEmu816_NativeWrapper_setY
  (JNIEnv *, jobject, jint v) {
    emu816::y.w = v;    
  }


//==============================================================================
// S19/28 Record Loader
//------------------------------------------------------------------------------

unsigned int toNybble(char ch)
{
	if ((ch >= '0') && (ch <= '9')) return (ch - '0');
	if ((ch >= 'A') && (ch <= 'F')) return (ch - 'A' + 10);
	if ((ch >= 'a') && (ch <= 'f')) return (ch - 'a' + 10);
	return (0);
}

unsigned int toByte(string &str, int &offset)
{
	unsigned int	h = toNybble(str[offset++]) << 4;
	unsigned int	l = toNybble(str[offset++]);

	return (h | l);
}

unsigned int toWord(string &str, int &offset)
{
	unsigned int	h = toByte(str, offset) << 8;
	unsigned int	l = toByte(str, offset);

	return (h | l);
}

unsigned long toAddr(string &str, int &offset)
{
	unsigned long	h = toByte(str, offset) << 16;
	unsigned long	m = toByte(str, offset) << 8;
	unsigned long	l = toByte(str, offset);

	return (h | m | l);
}

void load(const char *filename)
{
	ifstream	file(filename);
	string	line;

	if (file.is_open()) {
		cout << ">> Loading S28: " << filename << endl;

		while (!file.eof()) {
			file >> line;
			if (line[0] == 'S') {
				int offset = 2;

				if (line[1] == '1') {
					unsigned int count = toByte(line, offset);
					unsigned long addr = toWord(line, offset);
					count -= 3;
					while (count-- > 0) {
						emu816::setByte(addr++, toByte(line, offset));
					}
				}
				else if (line[1] == '2') {
					unsigned int count = toByte(line, offset);
					unsigned long addr = toAddr(line, offset);
					count -= 4;
					while (count-- > 0) {
						emu816::setByte(addr++, toByte(line, offset));
					}
				}
			}
		}
		file.close();
	}
	else
		cerr << "Failed to open file" << endl;
}