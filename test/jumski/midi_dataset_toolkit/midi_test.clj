(ns jumski.midi-dataset-toolkit.midi-test
  (:require [midje.sweet :refer :all]
            [test-helper :refer [load-trimmed-fixture fixture-path]]
            [jumski.midi-dataset-toolkit.midi :as m]))

(facts
  "events->steps"
  (fact "sorts notes, groups by quantized timestamp and converts"
        (let [note-1 {:timestamp 0 :note 0 :command :note-on}
              note-2 {:timestamp 1 :note 1 :command :note-on}
              note-3 {:timestamp 2 :note 2 :command :note-on}
              note-4 {:timestamp 101 :note 3 :command :note-on}
              note-5 {:timestamp 102 :note 4 :command :note-on}
              note-6 {:timestamp 103 :note 5 :command :note-on}

              events [note-6 note-5 note-4 note-3 note-2 note-1]]
          (m/events->steps events))
        => "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111\n00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111000")

  )
