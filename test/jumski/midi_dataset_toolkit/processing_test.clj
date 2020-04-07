(ns jumski.midi-dataset-toolkit.processing-test
  (:require [midje.sweet :refer :all]
            [jumski.midi-dataset-toolkit.processing :as processing]))

(set! *warn-on-reflection* true)

(fact
  "quantize-track aligns :timestamp of each note to grid"
  (let [track {:index 0
               :path "some/path"
               :note-ons [{:timestamp 49 :note 49}
                          {:timestamp 50 :note 50}]}]

    (sequence (map processing/quantize-track) [track])
    => [{:index 0
         :path "some/path"
         :note-ons [{:timestamp 45   :note 49}
                    {:timestamp 45 :note 50}]}]))

(fact
  "steppize-track groups notes by :timestamp and sorts the groups"
  (let [track {:index 0
               :path "some/path"
               :note-ons [{:timestamp 0   :note 1}
                          {:timestamp 100 :note 2}
                          {:timestamp 0   :note 3}
                          {:timestamp 200 :note 4}]}]
    (sequence (map processing/steppize-track) [track])
    => [{:index 0
         :path "some/path"
         :steps [[1 3] [2] [4]]}]))

(fact
  "binarize-track converts each step to line of 0s and 1s, encoding playing notes"
  (let [track {:index 0
               :path "some/path"
               :steps [[0 1 2] [3 4 5] [127]]}]
    (sequence (map processing/binarize-track) [track])
    => [{:index 0
         :path "some/path"
         :stepstring (let [lines ["00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111"
                                  "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111000"
                                  "10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"]]
                       (clojure.string/join "\n" lines))}]))
