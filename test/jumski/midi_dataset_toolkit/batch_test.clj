(ns jumski.midi-dataset-toolkit.batch-test
  (:require [midje.sweet :refer :all]
            [jumski.midi-dataset-toolkit.midi :as midi]
            [jumski.midi-dataset-toolkit.batch :as batch]))

(facts
  "about producing steps file pathnames from midi file paths"

  (fact
    (batch/steps-file-path "some/path/to/midi.file.MID" 7)
    => "some/path/to/midi.file.MID.part_07.steps")

  (fact
    (batch/steps-file-path "some/path/to/midi.file.mid" 7)
    => "some/path/to/midi.file.mid.part_07.steps")

  (fact
    (batch/steps-file-path "some/path/to/midi.file.midi" 7)
    => "some/path/to/midi.file.midi.part_07.steps"))
