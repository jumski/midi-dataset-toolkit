(ns jumski.midi-dataset-toolkit.core
  (:require [jumski.midi-dataset-toolkit.file :as file])
  (:gen-class))

(defn log-track-processing
  "Prints info about processing given track."
  [{:keys [index path]}]
  (do
    (println "Saving track" index "from" path)))

(defn -main
  "Runs `midi-file-to-steps-string` for each `arg` of `args` and prints to stdout"
  [dir]
  (if (empty? dir)
    (println "Please provide path to directory containing midi files!")
    (file/process-all-midi-files-in-dir! dir log-track-processing)))
