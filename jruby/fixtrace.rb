#File.read(	private void op_plp(int ea))

modes = []

File.read("/Users/rebecca/65x/jEmu816/jEmu816/src/main/java/jEmu816/cpu/CpuBase.java").
  each_line do |line|
    puts line
    if line =~ /if \(trace\) \{ traceAm/
      q1 = line.index('"')
      q2 = line.index('"', q1 + 1)
      text = line[q1..q2]
      modes.push(text)
    #  puts "\t\tif (trace) { traceOp(#{text}); }"
    end

  end

  modes.sort!
  modes.uniq!
  
  puts "switch(addressingMode) {"
  modes.each do |mode|
    puts "\tcase #{mode}: "
    puts "\t\t// TODO implement me."
    puts "\t\tbreak;"
  end
  puts "}"