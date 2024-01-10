require 'json'

def clear
  system 'clear'
end

def reload
  load 'createTests.rb'
end

MAXMEM = 16777216

def randByte
  (rand * 256).ceil - 1
end

def randWord
  (rand * 65536).ceil - 1
end

# nvmxdizc-E
def toFlags(baseVal, modeE)
  f = ""
  f += (baseVal & 128 == 128) ? 'N' : 'n'
  f += (baseVal & 64 == 64) ? 'V' : 'v'
  f += (baseVal & 32 == 32) ? 'M' : 'm'
  f += (baseVal & 16 == 16) ? 'X' : 'x'
  f += (baseVal & 8 == 8) ? 'D' : 'd'
  f += (baseVal & 4 == 4) ? 'I' : 'i'
  f += (baseVal & 2 == 2) ? 'Z' : 'z'
  f += (baseVal & 1 == 1) ? 'C' : 'c'
  f += '-'
  f += (modeE) ? 'E' : 'e'
  f
end



@memoryAffectingIns = ['ASL','DEC','INC','LSR','MVN','MVP',
  'ROL','ROR','STA','STX','STY','STZ','TRB','TSB']

@opcodes = []
fieldNames = []
File.read("OpcodeTable.dat").each_line do |line|
  if fieldNames.empty?
    line.split('|').each do |name|
      fieldNames.push(name.strip.to_sym)
    end
  else
    h = {}
    line.split('|').each_with_index do |item, idx|
      h[fieldNames[idx]] = item.strip
    end
    @opcodes[h[:OP].to_i(16)] = h
  end
end

#puts @opcodes.to_json

# all flags: nvmxdizc
#            84210000
#            00008421
count = 0
# do multiple iterations to get more random values
# each iteration creates 2560 tests, one for each mode
File.open("cputests.json", "wt") do |out|
  (0...10).each do |itr|
    (0..0xff).each do |opcode|
      #opcode = 0x00
      #puts opcode
      modes = ['emxd', 'eMxd', 'emXd', 'eMXd', 'emxD', 'eMxD', 'emXD', 'eMXD', 'E--d', 'E--D']
      ins = @opcodes[opcode]
      #puts ins
      modes.each do |mode|
        memStart = (rand * MAXMEM).ceil - 1
        code=[ins[:OP].to_i(16)]                    # tester will put code bytes @ pc + index of code array
        ram=[]                                      # pairs of [address, value]
        mnemonic=ins[:SYNTAX][0...3]
        modeM = mode.index('M')                     # 8 bit accumulator
        modeX = mode.index('X')                     # 8 bit indexes
        modeE = mode.index('E')
        modeD = mode.index('D')
        p = randByte() & 0xc7                       # turn off m,x,d   
        p = p | 0x20 if mode.index('M')
        p = p | 0x10 if mode.index('X')
        p = p | 0x08 if mode.index('D') 
        sp = randWord()
        sp &= 0xff if modeE                         # constrain SP if emulation
        dbr = modeE ? 0 : randByte()
        dp = modeE ? 0 : (256 * randByte())
        pc = randWord()
        pbr = modeE ? 0 : randByte()
        insSize = ins[:LEN].to_i
        error = false

        a = (modeE || modeM) ? randByte() : randWord()
        x = (modeE || modeX) ? randByte() : randWord()
        y = (modeE || modeX) ? randByte() : randWord()

        subForX = ins[:LEN].index('-x')
        subForM = ins[:LEN].index('-m')  

        if (modeE && (subForM || subForX))
          insSize -= 1
        elsif (!modeE && subForM && modeM)
          insSize -= 1
        elsif (!modeE && subForX && modeX)
          insSize -= 1
        end

        (1...insSize).each do |i|
          code.push randByte()
        end

        # write something to memory?  if so, always write two bytes
        if insSize > 1 && @memoryAffectingIns.index(mnemonic)
          if insSize > 2
            address = code[1] + 256*code[2]      
            ram.push([address.to_s(16), randByte().to_s(16)])
            ram.push([(address + 1).to_s(16), randByte().to_s(16)])
          else
            address = code[1]
            ram.push([address.to_s(16), randByte().to_s(16)])
          end
        end

        codeHex = code.map {|x| x.to_i.to_s(16)}

        status = sprintf("pbr:%02x pc:%04x sp:%04x f:%s a:%04x x:%04x y:%04x dp:%04x dbr:%02x",
          pbr, pc, sp, toFlags(p, modeE), a, x, y, dp, dbr)

        if opcode != 0xff
          testToWrite = {
            :name =>  "#{count} #{ins[:OP]} #{mode}",
            :initial => {
              :code => codeHex,
              :ram => ram,
              :status => status
            },
            :expected => { }
          }
          out.puts testToWrite.to_json
          count += 1
        end
      end
    end
  end 
end

puts "tests written: #{count}"