(ns jumski.midi-dataset-toolkit.core
  (:require [jumski.midi-dataset-toolkit.toolkit :as toolkit])
  (:gen-class))

(defn -main [& args]
  (if (empty? args)
    (println "Please provide path to midi file!")
    (doseq [path args
            :let [steps-string (toolkit/midi-file-to-steps-string path)]]
      (println steps-string))))
