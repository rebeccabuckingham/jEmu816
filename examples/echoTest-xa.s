;
; assembled via:
; xa -XCA65 -w -o echoTest-xa echoTest-xa.s
; then, run fixprg.rb echoTest.out f800
;
            .word $f800         ; prg header
            * = $f800

VRAM = $d700                    ; can write directly to VRAM
CRTC = $df00                    
KEYBOARD = $df02
KB_CONTROL_REG = KEYBOARD
KB_DATA_REG = KEYBOARD + 1

start:      clc                 ; switch to native mode
            xce            
            rep #$10            ; set index registers to 16-bit.
            .xl
            ldy #0
            ldx #0
            lda #32             ; clear screen the hard way
clrLoop:    sta VRAM,x
            inx
            cpx #1000
            bne clrLoop
            ldx #0
loop1:      lda message,y       ; output the hello message
            beq echoLoop
            sta VRAM,x
            inx
            cpx #1000           ; end of VRAM
            bne ok
            ldx #0
ok:         iny
            bne loop1
echoLoop:   lda KB_CONTROL_REG  ; check to see if there's a character ready
            beq echoLoop        ; 0 = no character, so loop.
            lda KB_DATA_REG
            sta VRAM,x          ; write character out
            inx
            cpx #1000           ; if we hit end of VRAM, go back to top.
            bne echoLoop
            ldx #0
            jmp echoLoop
            cop #8

message:    .byte "Echo test.                              "            
            .byte 0

.dsb $fffa - *                  ; padding to put the vectors in the right place.

            * = $fffa
            .word $0000         ; $fffa -> nmi
            .word start         ; $fffc -> reset
            .word $0000         ; $fffe -> irq
