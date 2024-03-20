INC_DIR = include
SRC_DIR = src
BUILD_DIR = build

CC = gcc
STATICSDL =
CFLAGS = -fPIC -I"$(JAVA_HOME)/include" -I"/usr/local/include/SDL2" -Iinclude 
LDFLAGS = -L/usr/local/lib -lSDL2 -shared
LIBNAME = libvera.jnilib

SRC = $(wildcard $(SRC_DIR)/*.c)
OBS = $(patsubst $(SRC_DIR)/%.c, $(BUILD_DIR)/%.o, $(SRC))

UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Linux)
	CFLAGS += -I"$(JAVA_HOME)/include/linux"
	LIBNAME = libvera.so
	STATICSDL = /usr/local/lib/libSDL2.a
endif
ifeq ($(UNAME_S),Darwin)
	CFLAGS += -I"$(JAVA_HOME)/include/darwin"
endif

all: create $(OBJS)
	echo $(shell uname -s)
	$(CC) $(CFLAGS) $(OBJS) $(LDFLAGS) -o $(BUILD_DIR)/$(LIBNAME) $(SRC) $(STATICSDL)

create:
	@mkdir -p $(BUILD_DIR)

clean:
	rm -rf $(BUILD_DIR)

.PHONY: create all clean
