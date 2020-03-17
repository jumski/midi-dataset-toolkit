(ns jumski.midi-dataset-toolkit.batch-test
  (:require [midje.sweet :refer :all]
            [jumski.midi-dataset-toolkit.midi :as midi]
            [jumski.midi-dataset-toolkit.batch :as b]
            [clojure.java.io]))

;; HELPERS

(defn- find-files-by-pattern
  "Returns `file-seq` of all files with names matching pattern."
  [dir patt]
  (let [dir (clojure.java.io/file dir)]
    (->> (file-seq dir)
         (filter #(.isFile %))
         (filter #(re-matches patt (.getPath %))))))

(def ^:private fixtures-root "test/resources")

(defn- fixture-path
  "Joins parts by /, prefixing by `fixtures-root`.
  Symbols are converted to their names."
  [& parts]
  (let [parts (map name parts)]
    (clojure.string/join "/" (cons fixtures-root parts))))

(defn- clean-fixture-dir
  "Removes all *.steps files in dir."
  [dirname]
  (let [dir (fixture-path dirname)
        patt #"(?i).*\.steps$"]
    (doseq [f (find-files-by-pattern dir patt)]
      (println "deleting" (.getPath f))
      (.delete f))))

;; TESTS

(facts
  "mididir->steps-files! uses midifile->steps-files!"
  (b/mididir->steps-files! "test/resources/fake-midis") => nil
  (provided
    (b/midifile->steps-files! "test/resources/fake-midis/a.mid") => nil
    (b/midifile->steps-files! "test/resources/fake-midis/subdir/b.mid") => nil
    (b/midifile->steps-files! "test/resources/fake-midis/subdir/subsubdir/c.mid") => nil))

(with-state-changes [(before :facts (clean-fixture-dir :real-midis))
                     (after :facts (clean-fixture-dir :real-midis))]
  (fact
    "midifile->steps-files! properly converts"
    (let [midipath (fixture-path :real-midis "c_major_scale.mid")
          steppath (fixture-path :real-midis "c_major_scale.mid.part_00.steps")
          expected (fixture-path :real-midis "c_major_scale.mid.part_00.expected")]
      (b/midifile->steps-files! midipath)
      (slurp steppath) => (slurp expected))))

(facts
  "proper path building for steps files"
  (fact
    (b/steps-file-path "some/path/to/midi.file.MID" 7)
    => "some/path/to/midi.file.MID.part_07.steps")

  (fact
    (b/steps-file-path "some/path/to/midi.file.mid" 7)
    => "some/path/to/midi.file.mid.part_07.steps")

  (fact
    (b/steps-file-path "some/path/to/midi.file.midi" 7)
    => "some/path/to/midi.file.midi.part_07.steps"))
