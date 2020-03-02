(ns jumski.midi-dataset-toolkit.toolkit
  (:require [overtone.midi.file :as midifile]))

;;; Private functions

(defn- note-on?
  "Returns true if event is a note on"
  [event]
  (and (= :note-on (:command event))
       (not (nil? (:note event)))))

(defn- simplify-event
  "Returns map that contains only :timestamp and :note"
  [event]
  (select-keys event [:timestamp :note]))

(defn- read-midi-tracks-from-path
  "Returns a list of tracks, each containing list of events."
  [path]

(defn- group-by-timestamp
  "Returns map of timestamp to list of events at given timestamp"
  [events]
  (->> (group-by :timestamp events)))

(defn- sort-numerically-by-first
  "Returns list sorted by first elements of colls, numerically, lower first"
  [colls]
  (sort #(< (first %1) (first %2)) colls))

(defn- extract-timestamp-and-note
  "Returns list of lists of notes"
  [time-events]
  (for [[timestamp events] time-events]
    [timestamp (map :note events)]))

(defn- convert-to-steps
  "Returns list of lists, each sublist containing :note values for all events
  occuring at the same :timestamp. Outer list is sorted by :timestamp,
  lower first."
  [note-on-events]
  (->> note-on-events
      (group-by-timestamp)
      (extract-timestamp-and-note)
      (sort-numerically-by-first)
      (map #(nth % 1))))

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

(defn- notes-to-bitstring-steps
  "Returns list of bitstrings, each representing one step with all notes,
  where 1s correspond to note being played at this step and 0s to notes being off.
  Bitstring is encoded in little-endian order (least significant bit is on the right)."
  [notes]
  (->> notes
    (convert-to-steps)
    (map notes-to-bitmask)
    (map bitmask-to-bitstring)))

;;; Public functions

(defn- events-to-steps-string
  [events]
  (->> events
      (println)
      (map (juxt :timestamp :note))
      (group-by first)))

(defn midi-file-to-steps-string
  "Returns string representing steps composed of bitstrings based on notes
  read from midi file at path."
  [path]
  (->> (midifile/midi-file path)
    (:tracks)
    (first)
    ; (map :events)
    ; (map events-to-steps-string)
    ; (filter (complement empty?))
    ; (take 1)
    ; (map #(map simplify-event))
    ))
    ; ; (map simplify-event)))
    ; (map #(notes-to-bitstring-steps))
    ; (clojure.string/join "\n")))

(let [xs [{:b [1 2 3]}
          {}]]
  (into {}
        (for [{:keys [a b]} xs] [a b])))
(into {} identity [[1 2] [1 3]])

(let [{a :b} {:b "XXX"}] a)
(let [{:keys [a b]} {:a "A" :b "B"}] [a b])

(let [midi (midifile/midi-file "resources/c_major_scale.mid")
      tracks (:tracks midi)
      track (nth tracks 1)
      events (:events track)
      note-ons (filter note-on? events)
      times-and-notes (map (juxt :timestamp :note) note-ons)
      times-to-events (group-by first times-and-notes)
      times-to-notes (for [[k v] times-to-events] [k (vec (map last v))])]
  (sort-by first times-to-notes))

