(ns jumski.midi-dataset-toolkit.batch-test
  (:require [midje.sweet :refer :all]
            [test-helper :refer [fixture-path load-trimmed-fixture clean-fixture-dir]]
            [jumski.midi-dataset-toolkit.batch :as b]))

(facts
  "mididir->steps-files! runs midifile->steps-files! for each file in directory"
  (b/mididir->steps-files! "test/resources/fake-midis") => nil
  (provided
    (b/midifile->steps-files! "test/resources/fake-midis/a.mid") => nil
    (b/midifile->steps-files! "test/resources/fake-midis/subdir/b.mid") => nil
    (b/midifile->steps-files! "test/resources/fake-midis/subdir/subsubdir/c.mid") => nil))

(with-state-changes [(before :facts (clean-fixture-dir :real-midis))
                     (after :facts (clean-fixture-dir :real-midis))]
  (fact
    "midifile->steps-files! creates multiple steps files for each track in midifile"
    (let [midipath   (fixture-path :real-midis "c_major_scale.mid")
          actual-f   "c_major_scale.mid.part_00.steps"
          expected-f "c_major_scale.mid.part_00.expected"]
      (b/midifile->steps-files! midipath)
      (load-trimmed-fixture :real-midis actual-f)
      => (load-trimmed-fixture :real-midis expected-f))))
