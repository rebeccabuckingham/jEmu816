#private void op_tsb(int ea) {

lastOp = ''
ops = []
File.read("/Users/rebecca/jEmu816/jEmu816/src/main/java/jEmu816/cpu/Cpu.java").each_line do |line|
  if line =~ /private void op_(...)\(int ea\) {/
    lastOp = $1
  elsif !lastOp.nil? && (line.index('machine.setByte') || line.index('machine.setWord'))
    ops.push "'" + lastOp.upcase + "'"
  end
end

puts ops.sort.uniq.join(',')