.word $c000
*=$c000
start     ldy #0
loop      lda msg,y
          beq done
;          sta $d000
          jsr chrout
          iny
          jmp loop
chrout    sta $d000
          rts
; - halt the emulator (WDM $ff)
done     .byte $42, $ff 
msg      .byte 'hello, world!', 0         
