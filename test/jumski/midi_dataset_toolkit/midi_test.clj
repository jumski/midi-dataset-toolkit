(ns jumski.midi-dataset-toolkit.midi-test
  (:require [midje.sweet :refer :all]
            [test-helper :refer [load-trimmed-fixture fixture-path]]
            [jumski.midi-dataset-toolkit.midi :as midi]))

(fact
  "about producting outputs for simple files"
  (midi/midi->steps (fixture-path :real-midis "c_major_scale.mid"))
  => (load-trimmed-fixture :real-midis "c_major_scale.mid.part_00.expected"))

(fact
  "about producting outputs for chords"
  (midi/midi->steps (fixture-path :real-midis "c_major_chord_4th_octave_60_64_67.mid"))
  => (load-trimmed-fixture :real-midis "c_major_chord_4th_octave_60_64_67.mid.part_00.expected"))
