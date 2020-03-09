(ns jumski.midi-dataset-toolkit.batch-test
  (:require [clojure.test :refer :all]
            [jumski.midi-dataset-toolkit.toolkit :as toolkit]
            [jumski.midi-dataset-toolkit.batch :as batch]))

(deftest smoke-test
  (testing "steps-file-path produces proper path"
    (is (= (batch/steps-file-path "some/path/to/midi.file.MID" 7)
           "some/path/to/midi.file.MID.part_07.steps"))
    (is (= (batch/steps-file-path "some/path/to/midi.file.mid" 7)
           "some/path/to/midi.file.mid.part_07.steps"))
    (is (= (batch/steps-file-path "some/path/to/midi.file.midi" 7)
          "some/path/to/midi.file.midi.part_07.steps"))))
