include Java
require 'json'

def setFlagsFromStr(str)
  p = 0
  str[0..7].chars.each_with_index do |c, idx|
    p += (2 ** (7 - idx)) if c.eql?(c.upcase)
  end

  $cpu.f.setP(p)
  $cpu.f.e = str[9].eql?('E') ? true : false;
end

def setRegisters(str)
  puts "setRegisters: #{str}"
  pc = 0
  pbr = 0
  str.split(' ').each do |item|
    (name, value) = item.split(':')
    case name
    when 'pbr'
      pbr = value.to_i(16)
      $cpu.pbr = pbr
    when 'pc'
      pc = value.to_i(16)
      $cpu.pc = pc
    when 'sp'
      $cpu.sp = value.to_i(16)
    when 'f'
      setFlagsFromStr(value)
    when 'a'
      $cpu.a.setRawValue(value.to_i(16))
    when 'x'
      $cpu.x.setRawValue(value.to_i(16))
    when 'y'
      $cpu.y.setRawValue(value.to_i(16))
    when 'dp'
      $cpu.dp = value.to_i(16)
    when 'dbr'
      $cpu.dbr = value.to_i(16)
    end
  end
  [pbr, pc]
end


def setupTest(testJson)
  # reset cpu.cycles
  $cpu.cycles = 0
  # set up registers & flags, including e -> returns [pbr, pc]
  (pbr, pc) = setRegisters(testJson['initial']['status'])

  addr = $util.join(pbr, pc)
  
  # poke code
  puts "poking code at: #{pbr.to_s(16)}:#{pc.to_s(16)}, addr: #{addr.to_s(16)}"
  testJson['initial']['code'].each_with_index do |b, idx|
    $machine.setByte(addr + idx, b.to_i(16))
  end

  # poke ram  
  testJson['initial']['ram'].each_with_index do |pair, idx|
    addr = pair[0].to_i(16)
    value = pair[1].to_i(16)
    puts "poking ram at: #{addr.to_s(16)}, #{value.to_s(16)}"
    $machine.setByte(addr, value)
  end
end

# $machine and $cpu are already set.
def runTest(testJson, outFile)
  @ok = true
  name = testJson['name']
  initialState = testJson['initial']
  expectedState = testJson['expected']
  finalState = {}

  setupTest(testJson)

  puts "before step(): #{$cpu.toString()}"
  $cpu.step()
  puts " after step(): #{$cpu.toString()}"

  # compare results
  statusRaw = $cpu.toString()
  #puts "raw: #{statusRaw}"

  _cycles = statusRaw.index('cycles:')
  _spaceAfter = statusRaw.index(' ', _cycles)
  status = statusRaw[0..._spaceAfter]

  #puts "status: '#{status}'"
  
  finalState['status'] = status
  @ok = false if !status.eql?(expectedState['status'])

  # compare ram here
  actualRam = []
  expectedState['ram'].each do |pair|
    address = pair[0].to_i(16)
    evalue = pair[1].to_i(16)
    value = $machine.getByte(address)
    actualRam.push([address.to_s(16), value.to_s(16)])
    @ok = false if (evalue != value)
  end
  finalState['ram'] = actualRam

  testJson['finalState'] = finalState
  testJson['result'] = @ok

  outFile.puts testJson.to_json

  @ok
end

def runTestFile(testFilename, resultsFilename, stopAfter)
  good = 0
  bad = 0
  File.open(resultsFilename, "wt") do |out|
    File.read(testFilename).each_line do |test|
      testJson = JSON.parse(test)
      if runTest(testJson, out)
        good += 1
      else
        bad += 1
      end

      if stopAfter && (bad >= stopAfter)
        break
      end
    end
    [good, bad]
  end
end

##### main

dir = "/Users/rebecca/65x/jEmu816"
classpathEntries = Dir.glob("#{dir}/distExpanded/jEmu816/lib/*")
classpathEntries.reject! {|x| x.end_with?('jEmu816.jar')}
classpathEntries.push "/Users/rebecca/65x/jEmu816/jEmu816/build/classes/java/main"

classpathEntries.each do |entry|
  $CLASSPATH << entry
end

java_import 'jEmu816.machines.TestMachine'

stopAfter = nil

if true
  $logger = Java::OrgSlf4j::LoggerFactory.getLogger("TestRunner")

  $machine = TestMachine.new
  $util = Java::JEmu816::Util
  $machine.reset()
  $cpu = $machine.getCpu()

  testFile = File.dirname(__dir__) + "/cpuTests/cputests_with_expected_results.json"
  resultsFile = File.dirname(__dir__) + "/cpuTests/cputests_with_final_results.json"

  (good, bad) = runTestFile(testFile, resultsFile, stopAfter)
  $logger.info("testfile: #{File.basename(testFile)}, #{good + bad} tests, good tests = #{good}, bad tests = #{bad}")

  totalTests = good + bad
  pctBad = (bad.to_f / totalTests.to_f) * 100
  pctGood = (good.to_f / totalTests.to_f) * 100

  $logger.info("testing complete.  #{totalTests} tests run.")
  $logger.info("good tests: #{good} (#{pctGood}) %")
  $logger.info(" bad tests: #{bad} (#{pctBad}) %")
end

