require 'json'

File.open("badTestsControl.json", 'wt') do |controlOut|
  File.open("badTestsResult.json", 'wt') do |resultOut|
    File.read("cputests_with_final_results.json").each_line do |text|
      json = JSON.parse(text)
      if (!json['result'])
        #puts 
        #puts text
        name = json['name']
        expected = {'name' => name, 'test' => json['expected']}
        finalState = {'name' => name, 'test' => json['finalState']}
        controlOut.puts expected.to_json
        resultOut.puts finalState.to_json
      end
    end
  end
end