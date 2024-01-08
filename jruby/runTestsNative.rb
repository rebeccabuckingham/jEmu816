include Java
require 'json'

def runTest(wrapper, testJson)
  # TODO: need to add getByte() method to wrapper to get ram results.
  # TODO: need to add getCycles() / resetCycles() method to wrapper.
  # from wrapper status update:
  # testJson['expected'] => { all fields from test + "cycles":n  }

  # will need to parse status:
  # pc:12:1234 sp:5432 f:nvmxdizc:E a:1234 x:5432 y:9988 dp:9800 dbr:34 cycles: 0

  # json object looks like this:
  # {"name":"0 00 emxd",
  #  "initial":{"code":["0"],"ram":[],"pbr":"5b",
  #     "pc":"1976","sp":"4e0","dbr":"1a","dp":"de00","p":"85",
  #     "a":"9ffb","x":"c96f","y":"6c90"},
  #  "expected":{}}

  wrapper.resetCycles()
  
  e = testJson['initial']['name'].index('E') ? 1 : 0
  pc = testJson['initial']['pc'].to_i(16)
  wrapper.setE(e)
  wrapper.setPBR(testJson['initial']['pbr'].to_i(16))
  wrapper.setPC(pc)
  wrapper.setSP(testJson['initial']['sp'].to_i(16))
  wrapper.setDBR(testJson['initial']['dbr'].to_i(16))
  wrapper.setDP(testJson['initial']['dp'].to_i(16))
  wrapper.setP(testJson['initial']['p'].to_i(16))
  wrapper.setA(testJson['initial']['a'].to_i(16))
  wrapper.setX(testJson['initial']['x'].to_i(16))
  wrapper.setY(testJson['initial']['y'].to_i(16))

  testJson['initial']['code'].each_with_index do |b, idx|
    wrapper.setByte(pc + idx, b.to_i(16))
  end

  testJson['initial']['ram'].each_with_index do |pair, idx|
    wrapper.setByte(pair[0], pair[1])
  end

  wrapper.step()
  status = wrapper.getStatus()

  #
  # TODO: parse status & put into 'expected' structure.
  # need to include ram[], after updating byte values.
  #

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

if false
testFile = '../cpuTests/cputests.json'
outFile = '../cpuTests/cputests_with_results.json'

count = 0
File.open(outFile, "wt") do |out|
  File.read(testFile).each_line do |test|
    testJson = JSON.parse(test)
    testJsonWithExpected = runTest(wrapper, testJson)
    out.puts testJsonWithExpected.to_json
    count += 1
  end
end

puts "there were #{count} tests in #{testFile}"
end