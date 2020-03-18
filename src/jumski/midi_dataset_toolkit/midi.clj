(ns jumski.midi-dataset-toolkit.midi
  (:require [overtone.midi.file :as midifile]
            [jumski.midi-dataset-toolkit.quantization :as q]))

;;; Private functions

(defn note-on?
  "Returns true if event is a note on"
  [event]
  (and (= :note-on (:command event))
       (not (nil? (:note event)))))

(defn- notes-to-bitmask
  "Returns 128-chars long string of 0s and 1s, representing all possible notes
  that should play at given step"
  [notes]
  (let [bitmask (vec (repeat 128 false))]
    (reduce #(assoc %1 %2 true) bitmask notes)))

(defn- bitmask-to-bitstring
  "Returns string of 0s and 1s, composed from bitmask in reverse order,
  1s correspond to true values and 0s to false values from bitmask."
  [bitmask]
  (let [bool-to-str {false "0" true "1"}
        str-bitmask (map bool-to-str (reverse bitmask))]
    (clojure.string/join str-bitmask)))

;;; Public functions

(defn track-has-note-ons?
  "Returns true if any of given track's events matches `midi-on?`."
  [{events :events}]
  (some note-on? events))

(defn note-on-tracks
  "Returns only tracks that have `note-on?` events."
  [{tracks :tracks}]
  (filter track-has-note-ons? tracks))

(defn events->steps
  "Converts list of events to multiline string of steps
  Takes list of hashes (from `overtone.midi.file`) from `:events` for some `:track`
  and converts it to multiline string of 0s and 1s. Each line is 128 chars long
  and represents a binary number encoded in little endian, where 1s are notes
  that are playing for given step."
  [events]
  (let [note-ons (filter note-on? events)
        times-and-notes (map (juxt :timestamp :note) note-ons)
        group-fn (comp (partial q/quantize 100) first)
        times-to-events (group-by group-fn times-and-notes)
        times-to-notes (for [[k v] times-to-events] [k (vec (map last v))])]
    (->> (sort-by first times-to-notes)
         (map last)
         (map notes-to-bitmask)
         (map bitmask-to-bitstring)
         (clojure.string/join "\n"))))

(defn midi->steps
  "Loads midi file and outputs one steps-stream concatenating step-streams for each track."
  [path]
  (->> (midifile/midi-file path)
      (:tracks)
      (map :events)
      (map events->steps)
      (filter (complement empty?))
      (clojure.string/join "\n")))

(comment
  (let [chord  "resources/c_major_chord_4th_octave_60_64_67.mid"
        scale  "resources/c_major_scale.mid"
        single "resources/single_note_c4.mid"]
    (->> (midifile/midi-file chord)
         (note-on-tracks)
         (map :events)
         (map events->steps)))
  )
