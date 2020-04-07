(ns jumski.midi-dataset-toolkit.midi-test
  (:require [midje.sweet :refer :all]
            [test-helper :refer [fixture-path]]
            [jumski.midi-dataset-toolkit.midi :as midi]
            [clojure.java.io :as io]))

(set! *warn-on-reflection* true)

(facts
  "load-tracks loads all tracks from given midi file"
  (let [midipath (fixture-path :real-midis "c_major_chord_4th_octave_60_64_67.mid")
        midifile (io/file midipath)]
    (midi/load-tracks midifile)
    => [{:index 1
         :note-ons [{:note 67 :timestamp 0}
                    {:note 64 :timestamp 0}
                    {:note 60 :timestamp 0}]
         :path "test/resources/real-midis/c_major_chord_4th_octave_60_64_67.mid"}]))

(facts
  "when loading midi throws error, return special non-note-on track"
  (let [midifile (io/file (fixture-path :fake-midis "a.mid"))
        path (.getPath ^java.io.File midifile)
        err (Exception. "I should be catched!!!")
        err-fn (fn [_ _])]
    (midi/load-tracks midifile err-fn) => [{:error err}]
    (provided
      (overtone.midi.file/midi-file path) =throws=> err)))

(facts
  "when loading midi throws error, log to screen by default"
  (let [midifile (io/file (fixture-path :fake-midis "a.mid"))
        path (.getPath ^java.io.File midifile)
        err (Exception. "I should be catched!!!")]
    (with-out-str
      (midi/load-tracks midifile))
    => #(re-find #"I should be catched!!!" %)
    (provided
      (overtone.midi.file/midi-file path) =throws=> err)))

(facts
  "when loading midi throws error and logger-fn provided, call it"
  (let [midifile (io/file (fixture-path :fake-midis "a.mid"))
        path (.getPath ^java.io.File midifile)
        err (Exception. "I should be catched!!!")
        err-fn #(print (.getPath ^java.io.File %1) (class %2))]
    (with-out-str
      (midi/load-tracks midifile err-fn))
    => "test/resources/fake-midis/a.mid java.lang.Exception"
    (provided
      (overtone.midi.file/midi-file path) =throws=> err)))
