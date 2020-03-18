(ns jumski.midi-dataset-toolkit.sandbox
  (:require [overtone.midi.file :as omidi]
            [jumski.midi-dataset-toolkit.midi :as midi]
            [jumski.midi-dataset-toolkit.batch :as batch]
            [jumski.midi-dataset-toolkit.quantization :as quantization]
            [test-helper :as thelper]))

(def midipath "resources/dubroom_-_01_nimrods_defeat.mid")
(def midifile (omidi/midi-file midipath))

(keys midifile)
; (:type :division-type :resolution :sequence :usecs :properties :tracks)

(select-keys midifile [:type :division-type :resolution :usecs :properties])
(def tracks (midi/note-on-tracks midifile))
(def track (nth tracks 3))

;;; track and events inspection

(defn inspect-track
  "Outputs some info about track."
  [track]
  (select-keys track [:type :size]))

(inspect-track (first tracks))
((juxt inspect-track #(count (:events %)))
 track)

(defn inspect-events
  "Outputs some info about events."
  [events]
  (map keys events))
(->> (inspect-events (:events track))
     flatten
     distinct)
(->> (nth (:tracks midifile) 15)
     :events
     (filter midi/note-on?)
     (map :timestamp)
     (group-by (partial quantization/quantize 100))
     (sort-by first))



