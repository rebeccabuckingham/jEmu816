def padToSize(str, size)
  sprintf("%-#{size}s", str)
end

puts "package jEmu816;"
puts "\npublic class DisassemblerBase {"

modes = []

File.read("../opcodeList.dat").each_line do |line|
  (opcode,name,mode,bytes,cycles,flags,info) = line.split('|')
  modes.push(mode)
end
modes.sort!
modes.uniq!

first = true
newline = true
count = 0
perline = 5
modes.each do |mode|    #  print "\t\tAM.#{mode.upcase}"
  if first 
    first = false
    print "\tpublic enum AM {\n\t\t"
  else
    if (count % perline) == 0 
      if newline
        newline = false
      else
        print ",\n\t\t" 
      end
    else
      print ", "
    end
    print padToSize("#{mode.upcase}",4)

    count += 1

  end
end

puts "\n\t}\n\n"

first = true
newline = true
count = 0
perline = 8
File.read("../opcodeList.dat").each_line do |line|
  if first 
    first = false
    print "\tpublic static final String[] opcodeNames = {\n\t\t"
  else
    if (count % perline) == 0 
      if newline
        newline = false
      else
        print ",\n\t\t" 
      end
    else
      print ", "
    end

    (opcode,name,mode,bytes,cycles,flags,info) = line.split('|')
    
    print (padToSize('"' + name + '"',6))
    count += 1
  end
end

print " };\n\n"

first = true
newline = true
count = 0
File.read("../opcodeList.dat").each_line do |line|
  if first 
    first = false
    print "\tpublic static final int[] bytes = {\n\t\t"
  else
    if (count % perline) == 0 
      if newline
        newline = false
      else
        print ",\n\t\t" 
      end
    else
      print ", "
    end

    (opcode,name,mode,bytes,cycles,flags,info) = line.split('|')
    
    print bytes
    count += 1
  end
end
print " };\n\n"

first = true
newline = true
count = 0
File.read("../opcodeList.dat").each_line do |line|
  if first 
    first = false
    print "\tpublic static final AM[] addressModes = {\n\t\t"
  else
    if (count % perline) == 0 
      if newline
        newline = false
      else
        print ",\n\t\t" 
      end
    else
      print ", "
    end

    (opcode,name,mode,bytes,cycles,flags,info) = line.split('|')
    
    print padToSize("AM.#{mode.upcase}", 7);
    count += 1
  end
end
print "\n\t};\n\n"

puts "}"