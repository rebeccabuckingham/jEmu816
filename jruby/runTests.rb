include Java
require 'json'

def compare(name, item, me, you)
  if !you.eql?(me)
    if me.class.eql?(Integer)
      me = me.to_s(16)
      you = you.to_s(16)
    end
    $logger.info("testName: #{name}, item: #{item}, control: #{you}, test: #{me}")
    @ok = false
  end
end

def runTest(testJson)
  @ok = true
  name = testJson['name']
  initialState = testJson['initial']
  finalState = testJson['final']

  $cpu.pc = initialState['pc']
  $cpu.sp = initialState['s']
  $cpu.pbr = initialState['pbr']
  $cpu.dbr = initialState['dbr']
  $cpu.dp = initialState['d']
  $cpu.x.setRawValue(initialState['x'])
  $cpu.y.setRawValue(initialState['y'])
  $cpu.a.setRawValue(initialState['a'])
  $cpu.f.setP(initialState['p'])
  $cpu.f.e = initialState['e'] == 1

  cpuStatusStart = "cpu initial state: #{$cpu.toString()}"

  initialState['ram'].each do |entry|
    $machine.setByte(entry[0], entry[1])
  end

  $cpu.step()

  cpuStatusEnd = "  cpu final state: #{$cpu.toString()}"

  compare(name, 'pc', $cpu.pc, finalState['pc'])
  compare(name, 'sp', $cpu.sp, finalState['s'])
  compare(name, 'pbr', $cpu.pbr, finalState['pbr'])
  compare(name, 'dbr', $cpu.dbr, finalState['dbr'])
  compare(name, 'dp', $cpu.dp, finalState['d'])
  compare(name, 'a', $cpu.a.getRawValue(), finalState['a'])
  compare(name, 'x', $cpu.x.getRawValue(), finalState['x'])
  compare(name, 'y', $cpu.y.getRawValue(), finalState['y'])
  compare(name, 'p', $cpu.f.getP(), finalState['p'])
  compare(name, 'e', $cpu.f.e, finalState['e'] == 1)

  finalState['ram'].each do |entry|
    compare(name, "memory #{entry[0]}", $machine.getByte(entry[0]), entry[1])
  end

  if !@ok
    $logger.info('--------------------------------------------------------------')
    $logger.error("test #{name} failed.")
    $logger.debug(cpuStatusStart)
    $logger.debug(cpuStatusEnd)
    $logger.info('--------------------------------------------------------------')

  end

  @ok
end

def runTestFile(testFilename, stopAfter)
  good = 0
  bad = 0
  testBatch = JSON.parse(File.read(testFilename))
  testBatch.each_with_index do |testJson, idx|
    if runTest(testJson)
      good += 1
    else
      bad += 1
    end
    break if (bad >= stopAfter)
  end
  [good, bad]
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

if true
  $logger = Java::OrgSlf4j::LoggerFactory.getLogger("TestRunner")

  $machine = TestMachine.new
  #$util = Java::JEmu816::Util
  $machine.reset()
  $cpu = $machine.getCpu()

  totalGood = 0
  totalBad = 0

  testDir = "/Users/rebecca/ProcessorTests/65816/v1"
  #Dir.glob("#{testDir}/*").each do |testFile| 
    testFile = "/Users/rebecca/ProcessorTests/65816/v1/a9.n.json"
    stopAfter = 1

    (good, bad) = runTestFile(testFile, stopAfter)
    $logger.info("testfile: #{File.basename(testFile)}, #{good + bad} tests, good tests = #{good}, bad tests = #{bad}")

    totalGood += good
    totalBad += bad
  #end

  $logger.info("testing complete.")
  $logger.info("#{totalGood + totalBad} tests, total good tests = #{totalGood}")
  $logger.info("     total bad tests = #{totalBad}")
end

