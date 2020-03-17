(ns jumski.midi-dataset-toolkit.core
  (:require [jumski.midi-dataset-toolkit.batch :as batch])
  (:gen-class))

(defn -main
  "Runs `midi-file-to-steps-string` for each `arg` of `args` and prints to stdout"
  [mididir]
  (if (empty? mididir)
    (println "Please provide path to directory containing midi files!")
    (batch/mididir->steps-files! mididir)))
