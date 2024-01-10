require 'json'
require 'colorize'

def pairsToHash(pairs)
  h = {}
  pairs.split(' ').each do |pair|
    (name, value) = pair.split(':')
    h[name] = value
  end
  h
end

def hexOut(array)
  str = ""
  array.each do |item|
    str += ' ' if str.length > 0
    str += sprintf("%02x", item.to_i(16))
  end
  "[#{str}]"
end

def joinBytes(array)
  acc = ""
  digits = 0
  array.reverse.each do |v|
    acc += sprintf("%02x", v.to_i(16)) 
    #print "#{acc} "
  end
  acc
end

# a very sad disassembler
# given: "ORA $32,S".gsub(/(\d+)/, 99.to_s(16))
def disasm(code)
  opcode = $opcodes[code[0].to_i(16)]
  syntax = opcode[:SYNTAX]
  if (code.length > 1)
    syntax.gsub!(/\$(\w+)/, '$' + joinBytes(code[1..-1]))
  end
  syntax
end

def compare(code, name, expStatus, expRam, finStatus, finRam)
  print "#{name}:\t".colorize(:light_white)
  print hexOut(code).colorize(:white)
  opcode = $opcodes[code[0].to_i(16)]
  baseIns = opcode[:SYNTAX][0..2]
  puts " #{disasm(code)}".colorize(:yellow)
  expStatusHash = pairsToHash(expStatus)
  finStatusHash = pairsToHash(finStatus)
  
  expStatusHash.each do |name, valueA|
    valueB = finStatusHash[name]
    if !valueA.eql?(valueB)
      print "  field: #{name}".colorize(:light_white)
      print " expected: ".colorize(:white)
      print valueA.colorize(:light_green)
      print " actual: ".colorize(:white)
      print valueB.colorize(:light_red)
    end
  end

  if expRam.length > 0 || finRam.length > 0
    print " ram".colorize(:light_white)
    print "  expected: ".colorize(:white)
    print expRam.to_s.colorize(:light_green)
    print " actual: ".colorize(:white)
    print finRam.to_s.colorize(:light_red)
  end

  puts
  baseIns
end

# load opcodes to make it easier to identify problems
$opcodes = []
fieldNames = []
File.read("OpcodeTable.dat").each_line do |line|
  if fieldNames.empty?
    line.split('|').each do |name|
      fieldNames.push(name.strip.to_sym)
    end
  else
    h = {}
    line.split('|').each_with_index do |item, idx|
      h[fieldNames[idx]] = item.strip
    end
    $opcodes[h[:OP].to_i(16)] = h
  end
end

if false
$opcodes.each_with_index do |item, i|
  puts "i (#{i.to_s(16)}): #{item.to_s}"
end
end

maxCount = nil
if true
  # allow to specify max number of errors to show.
  if (!ARGV[0].nil?) 
    maxCount = ARGV[0].to_i
  end
end
count = 0

baseInstructionsFound = {}

# run through the tests to highlight problems
File.read("cputests_with_final_results.json").each_line do |text|
  json = JSON.parse(text)
  if (!json['result'])
    name = json['name']
    expected = json['expected']
    final = json['finalState']

    code = json['initial']['code']

    # comment this back out...
    if ['f5','f7','f9','fd','e1','e3','e5'].index(code[0])
    baseIns = compare(code, name, expected['status'], expected['ram'], final['status'], final['ram'])

    baseInsCount = baseInstructionsFound[baseIns]
    baseInsCount = 0 if baseInsCount.nil?
    baseInsCount += 1
    baseInstructionsFound[baseIns] = baseInsCount

    count += 1
    break if !maxCount.nil? && count >= maxCount  
    # comment this out too.
    end
  end
end

puts "bad instruction hot list"
tmp = []
baseInstructionsFound.each do |k,v|
  tmp.push [k,v]
end
tmp.sort!{|a,b| b[1] <=> a[1]}
tmp.each do |p|
  puts "#{p[0]}: #{p[1]}"
end


