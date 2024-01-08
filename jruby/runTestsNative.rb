include Java
require 'json'

def runTest(wrapper, testJson)
  # TODO: need to add getByte() method to wrapper to get ram results.
  # TODO: need to add getCycles() / resetCycles() method to wrapper.
  # from wrapper status update:
  # testJson['expected'] => { all fields from test + "cycles":n  }
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

testFile = '/Users/rebecca/cpuTests/cputests.json'
outFile = '/Users/rebecca/cpuTests/cputests_with_results.json'

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
