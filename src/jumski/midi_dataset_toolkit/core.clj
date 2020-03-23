(ns jumski.midi-dataset-toolkit.core
  (:require [jumski.midi-dataset-toolkit.file :as file])
  (:gen-class))

(defn- logger-fn
  "Prints info about processing given track."
  [{:keys [index path]}]
  (do
    (println "Saving track" index "from" path)))

(defn -main
  "Runs `process-all-midi-files-in-dir!` for given `dir` if not empty."
  [dir]
  (if (seq dir)
    (file/process-all-midi-files-in-dir! dir logger-fn)
    (println "Please provide path to directory containing midi files!")))
