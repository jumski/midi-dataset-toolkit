(ns jumski.midi-dataset-toolkit.midi
  (:require [overtone.midi.file]))

; Helpers

(defn note-on?
  "Returns true if event is a note on"
  [event]
  (and (= :note-on (:command event))
       (not (nil? (:note event)))))

(def ^:private only-note-ons
  "Transducer, filters only note-on? events and simplifies them."
  (comp
    (filter note-on?)
    (map #(select-keys % [:timestamp :note]))))

; Public

(defn load-tracks
  "Returns sequence of Track's for given midifile at path
  Skips any tracks that does not have any note-ons."
  [midifile]
  (let [path (.getPath midifile)
        {tracks :tracks} (overtone.midi.file/midi-file path)]
    (for [[index {events :events}] (map-indexed list tracks)
          :when (some note-on? events)]
      {:index index :path path :note-ons (sequence only-note-ons events)})))

