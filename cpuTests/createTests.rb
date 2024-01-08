require 'json'

def clear
  system 'clear'
end

def reload
  load 'createTests.rb'
end

MAXMEM = 16777216

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
        memStart = (rand * MAXMEM).ceil
        code=[ins[:OP].to_i(16)]                    # tester will put code bytes @ pc + index of code array
        ram=[]                                      # pairs of [address, value]
        mnemonic=ins[:SYNTAX][0...3]
        modeM = mode.index('M')                     # 8 bit accumulator
        modeX = mode.index('X')                     # 8 bit indexes
        modeE = mode.index('E')
        modeD = mode.index('D')
        p = (rand * 256).ceil & 0xc7                # turn off m,x,d   
        p = p | 0x20 if mode.index('M')
        p = p | 0x10 if mode.index('X')
        p = p | 0x08 if mode.index('D') 
        sp = (rand * 65536).ceil
        sp &= 0xff if modeE                         # constrain SP if emulation
        dbr = modeE ? 0 : (rand * 256).ceil
        dp = modeE ? 0 : (256 * (rand * 256).ceil)
        pc = (rand * 65536).ceil
        pbr = modeE ? 0 : (rand * 256).ceil
        insSize = ins[:LEN].to_i
        error = false

        a = (rand * ((modeE || modeM) ? 256 : 65536)).ceil
        x = (rand * ((modeE || modeX) ? 256 : 65536)).ceil
        y = (rand * ((modeE || modeX) ? 256 : 65536)).ceil

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
          code.push ((rand * 256).ceil)
        end

        # write something to memory?  if so, always write two bytes
        if insSize > 1 && @memoryAffectingIns.index(mnemonic)
          if insSize > 2
            address = code[1] + 256*code[2]      
            ram.push([address.to_s(16), (rand * 256).ceil.to_s(16)])
            ram.push([(address + 1).to_s(16), (rand * 256).ceil.to_s(16)])
          else
            address = code[1]
            ram.push([address.to_s(16), (rand * 256).ceil.to_s(16)])
          end
        end

        codeHex = code.map {|x| x.to_i.to_s(16)}

        if opcode != 0xff
          testToWrite = {
            :name =>  "#{count} #{ins[:OP]} #{mode}",
            :initial => {
              :code => codeHex,
              :ram => ram,
              :pbr => pbr.to_s(16),
              :pc => pc.to_s(16),
              :sp => sp.to_s(16),
              :dbr => dbr.to_s(16),
              :dp => dp.to_s(16),
              :p => p.to_s(16),
              :a => a.to_s(16),
              :x => x.to_s(16),
              :y => y.to_s(16)
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