include Java
require 'json'

def updateTest(wrapper, testJson)
  expected = { 'status' => wrapper.getStatus() }
  ram = []

  testJson['initial']['ram'].each_with_index do |pair, idx|
    (addressStr, originalValueStr) = pair
    address = addressStr.to_i(16)
    value = wrapper.getByte(address)
    ram.push([addressStr, value.to_s(16)])
  end

  expected['ram'] = ram
  testJson['expected'] = expected

  puts "setting expected['ram'] to #{ram.to_s}"

  testJson
end

def setFlagsFromStr(wrapper, str)
  wrapper.setE(str[9].eql?('E') ? 1 : 0)
  p = 0
  str[0..7].chars.each_with_index do |c, idx|
    p += (2 ** (7 - idx)) if c.eql?(c.upcase)
  end
  wrapper.setP(p)
end

# pbr:e7 pc:6d9a sp:0bf5 f:nvmxdIZC-e a:0d00 x:e698 y:2630 dp:f300 dbr:b0
def setRegisters(wrapper, str)
  puts "setRegisters: #{str}"
  pc = 0
  pbr = 0
  str.split(' ').each do |item|
    (name, value) = item.split(':')
    case name
    when 'pbr'
      pbr = value.to_i(16)
      wrapper.setPBR(pbr)
    when 'pc'
      pc = value.to_i(16)
      wrapper.setPC(pc)
    when 'sp'
      wrapper.setSP(value.to_i(16))
    when 'f'
      setFlagsFromStr(wrapper, value)
    when 'a'
      wrapper.setA(value.to_i(16))
    when 'x'
      wrapper.setX(value.to_i(16))
    when 'y'
      wrapper.setY(value.to_i(16))
    when 'dp'
      wrapper.setDP(value.to_i(16))
    when 'dbr'
      wrapper.setDBR(value.to_i(16))
    end
  end
  [pbr, pc]
end

def setupTest(wrapper, testJson)
  (pbr, pc) = setRegisters(wrapper, testJson['initial']['status'])

  addr = (pbr << 16) + pc
  puts "poking code at: #{pbr.to_s(16)}:#{pc.to_s(16)}, addr: #{addr.to_s(16)}"

  testJson['initial']['code'].each_with_index do |b, idx|
    wrapper.setByte(addr + idx, b.to_i(16))
  end

  testJson['initial']['ram'].each_with_index do |pair, idx|
    addr = pair[0].to_i(16)
    value = pair[1].to_i(16)
    puts "poking ram at: #{addr.to_s(16)}, #{value.to_s(16)}"
    wrapper.setByte(addr, value)
  end

end

def runTest(wrapper, testJson)
  wrapper.resetCycles()
  setupTest(wrapper, testJson)
  puts "before step(): #{wrapper.getStatus()}"
  wrapper.step()
  puts " after step(): #{wrapper.getStatus()}"
  testJson = updateTest(wrapper, testJson)  
  testJson
end

dir = "/Users/rebecca/65x/jEmu816"
classpathEntries = Dir.glob("#{dir}/distExpanded/jEmu816/lib/*")
classpathEntries.reject! {|x| x.end_with?('jEmu816.jar')}
classpathEntries.push "/Users/rebecca/65x/jEmu816/jEmu816/build/classes/java/main"

classpathEntries.each do |entry|
  $CLASSPATH << entry
end

puts 'importing wrapper'
java_import 'jEmu816.NativeWrapper'

puts "library path: #{Java::JavaLang::System.getProperty('java.library.path')}"

puts 'creating wrapper instance'
wrapper = NativeWrapper.new

puts 'initializing wrapper'
wrapper.init()

puts 'resetting wrapper'
wrapper.reset()

puts "wrapper status: #{wrapper.getStatus()}"
wrapper.resetCycles()

if true
testFile = '../cpuTests/cputests.json'
outFile = '../cpuTests/cputests_with_expected_results.json'

count = 0
File.open(outFile, "wt") do |out|
  File.read(testFile).each_line do |test|
    testJson = JSON.parse(test)
    testJsonWithExpected = runTest(wrapper, testJson)
    out.puts testJsonWithExpected.to_json
    count += 1

    if (count % 1000) == 0
      puts "#{count} tests executed/written."
    end
  end
end

puts "\nthere were #{count} tests in #{testFile}"
end