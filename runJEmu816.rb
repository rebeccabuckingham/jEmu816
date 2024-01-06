#!/usr/bin/env ruby

classpathEntries = Dir.glob("#{__dir__}/distExpanded/jEmu816/lib/*")
classpathEntries.reject! {|x| x.end_with?('jEmu816.jar')}
classpathEntries.push "./jEmu816/build/classes/java/main"
classpath = classpathEntries.join(':')
jnipath = Dir.glob("#{__dir__}/emu816/build/lib/main/debug")[0]
args = ARGV.join(' ')

cmd = "#{ENV['JAVA_HOME']}/bin/java -Djava.library.path=#{jnipath} -classpath #{classpath} jEmu816.Main #{args}"

system cmd


