def allDigits(str)
  str.chars.each do |c|
    if (c >= '0' && c <= '9') || (c >= 'a' && c < 'f') || (c >= 'A' && c <= 'F')
      # nothing
    else
      return false
    end
  end
end

preSection = false
tableSection = false
count = 0
File.open("OpcodeTable.dat", "wt") do |out|
  out.puts "OP|LEN|CYCLES     |MODE     |nvmxdizc|e|SYNTAX"
  File.read("65c816 Opcodes.html").each_line do |line|
    if line =~ /<pre>/ 
      preSection = true 
    elsif line =~ /<\/pre>/
      preSection = false 
    elsif line =~ /(..) (...) (...........) (.........) (........) (.) (.+)/
      if allDigits($1)
        out.puts [$1,$2,$3,$4,$5,$6,$7].join('|')
        count += 1
      end
    end
  end 
end

puts "count: #{count}"

