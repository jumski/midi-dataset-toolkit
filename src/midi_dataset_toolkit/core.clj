(ns midi-dataset-toolkit.core
  (:require [overtone.midi.file :as midifile])
  (:gen-class))

(defn note-on?
  "Returns true if event is a note on"
  [event]
  (and (= :note-on (:command event))
       (not (nil? (:note event)))))

(defn simplify-event
  "Returns map that contains only :timestamp and :note"
  [event]
  (select-keys event [:timestamp :note]))

(defn read-note-ons-from-file
  "Returns a list of events, flattened from all tracks in midi path from path"
  [path]
  (->> (midifile/midi-file path)
      (:tracks)
      (map :events)
      (flatten)
      (filter note-on?)
      (map simplify-event)))

(defn group-by-timestamp
  "Returns map of timestamp to list of events at given timestamp"
  [events]
  (->> (group-by :timestamp events)))

(defn sort-numerically-by-first
  "Returns list sorted by first elements of colls, numerically, lower first"
  [colls]
  (sort #(< (first %1) (first %2)) colls))

(defn extract-timestamp-and-note
  "Returns list of lists of notes"
  [time-events]
  (for [[timestamp events] time-events]
    [timestamp (map :note events)]))

(defn convert-to-steps
  "Returns list of lists, each sublist containing :note values for all events
   occuring at the same :timestamp. Outer list is sorted by :timestamp,
   lower first."
  [note-on-events]
  (->> note-on-events
      (group-by-timestamp)
      (extract-timestamp-and-note)
      (sort-numerically-by-first)
      (map #(nth % 1))))

(defn notes-to-bitmask
  "Returns 128-chars long string of 0s and 1s, representing all possible notes
   that should play at given step"
  [notes]
  (let [bitmask (vec (repeat 128 false))]
    (reduce #(assoc %1 %2 true) bitmask notes)))

(defn bitmask-to-bitstring
  "Returns string of 0s and 1s, composed from bitmask in reverse order,
   1s correspond to true values and 0s to false values from bitmask."
  [bitmask]
  (let [bool-to-str {false "0" true "1"}
        str-bitmask (map bool-to-str (reverse bitmask))]
    (clojure.string/join str-bitmask)))

(defn notes-to-bitstring-steps
  "Returns list of bitstrings, each representing one step with all notes,
   where 1s correspond to note being played at this step and 0s to notes being off.
   Bitstring is encoded in little-endian order (least significant bit is on the right)."
  [notes]
  (->> notes
    (convert-to-steps)
    (map notes-to-bitmask)
    (map bitmask-to-bitstring)))

(defn midi-file-to-steps-string
  "Returns string representing steps composed of bitstrings based on notes
   read from midi file at path."
  [path]
  (->> (read-note-ons-from-file path)
    (notes-to-bitstring-steps)
    (clojure.string/join "\n")))

(defn -main [& args]
  (if (empty? args)
    (println "Please provide path to midi file!")
    (doseq [path args
            :let [steps-string (midi-file-to-steps-string path)]]
      (println steps-string))))

; (-> "resources/c_major_scale.mid"
;     (midi-file-to-steps-string))
