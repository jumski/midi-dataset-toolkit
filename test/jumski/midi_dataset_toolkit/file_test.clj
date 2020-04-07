(ns jumski.midi-dataset-toolkit.file-test
  (:require [midje.sweet :refer :all]
            [test-helper :refer [clean-fixture-dir! fixture-path load-trimmed-fixture]]
            [jumski.midi-dataset-toolkit.file :as file]))

(set! *warn-on-reflection* true)

(with-state-changes
  [(before :contents (do
                    (clean-fixture-dir! :real-midis)
                    (file/process-all-midi-files-in-dir!
                      (fixture-path :real-midis))))]
  (fact
    "processes simple scale progression"
    (load-trimmed-fixture :real-midis "c_major_scale.mid.part_01.steps")
    => (load-trimmed-fixture :real-midis "c_major_scale.mid.part_01.expected"))
  (fact
    "processes simple chord progression"
    (load-trimmed-fixture :real-midis "c_major_chord_4th_octave_60_64_67.mid.part_01.steps")
    => (load-trimmed-fixture :real-midis "c_major_chord_4th_octave_60_64_67.mid.part_01.expected"))

  (fact
    "processes files that needs quantization"
    (load-trimmed-fixture :real-midis "to_be_quantized.mid.part_01.steps")
    => (load-trimmed-fixture :real-midis "to_be_quantized.mid.part_01.expected")))
