(ns jumski.midi-dataset-toolkit.batch
  (:require [overtone.midi.file :as midifile]))

(def MIDIFILE_REGEX #".*\.midi?$")

(defn- find-midis
  "Returns `file-seq` of all `*.mid{,i}` files in given `dir`."
  [dir]
  (let [dir (clojure.java.io/file dir)]
    (->> (file-seq dir)
         (filter #(.isFile %))
         (filter #(re-matches MIDIFILE_REGEX (.getPath %))))))


(comment
  (def dub_midis "/home/jumski/Documents/midis/Dub_MIDIRip")
  (def resources_midi "resources/")

  (->> (find-midis resources_midi)
       (take 1)
       (map #(.getPath %))
       (map midifile/midi-file)
       (first)
       (:tracks)
       (last)
       (:events)
       (filter :velocity)
       ; (map :channel)
       ; (map keys)
       ; (flatten)
       ; (set)
       (println))
)

