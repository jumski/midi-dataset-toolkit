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

(defn- events->bitstring [events]
  (->> events
       (map :note)
       (filter identity)
       notes-to-bitmask
       bitmask-to-bitstring))

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
  (letfn [(group-fn [{t :timestamp}] (q/quantize 100 t))]
    (->> events
      (group-by group-fn)
      (sort-by first)
      (map last)
      (map events->bitstring)
      (clojure.string/join "\n"))))
