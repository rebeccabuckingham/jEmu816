#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>
#include <SDL.h>

// implement these later
void psg_reset(void) {

}

void pcm_reset(void) {

}

void machine_dump(const char* msg) {

}

void machine_reset() { }
void machine_nmi() { }
void machine_paste(const char* text) { }
void machine_toggle_warp() { }
void sdcard_attach() { }
void sdcard_detach() { }
void mouse_button_down(int num) { }
void mouse_button_up(int num) { }
void mouse_move(int x, int y) { }
void mouse_set_wheel(int y) { }
void mouse_send_state() { }
void audio_render() { }
void joystick_add(int index) { }
void joystick_remove(int index) { }
void joystick_button_down(int instance_id, uint8_t button) { }
void joystick_button_up(int instance_id, uint8_t button) { }

bool grab_mouse = false;
bool warp_mode = false;
bool disable_emu_cmd_keys = false;
bool enable_midline = false;

bool log_video = false;

SDL_KeyboardEvent keyboardEvent;

// TODO this needs to be moved to smc.c 
uint8_t activity_led = 0;

uint32_t key_modifiers = 0;
uint32_t key_scancode = 0;

SDL_Event event;

void handle_keyboard(bool down, u_int32_t mod, SDL_Keycode sym, SDL_Scancode scancode) { 
  printf("handle_keyboard(%d, %d, %d, %d)\n", down, mod, sym, scancode);
  //key_modifiers = mod;
  //key_scancode = scancode;
}
