(ns midi-dataset-toolkit.core
  (:require [overtone.midi.file :as midifile]))

(defn note-on?
  "Returns true if event is a note on"
  [event]
  (and (= :note-on (:command event))
       (not (nil? (:note event)))))

(defn read-note-ons-from-file
  "Returns a list of events, flattened from all tracks in midi path from path"
  [path]
  (->> (midifile/midi-file path)
      (:tracks)
      (map :events)
      (flatten)
      (filter note-on?)))

(defn group-by-timestamp
  "Returns map of timestamp to list of events at given timestamp"
  [events]
  (group-by :timestamp events))

(->> (read-note-ons-from-file "resources/c_major_scale.mid")
    (group-by-timestamp)
    (sort #(< (first %1) (first %2)))
    (map count))
