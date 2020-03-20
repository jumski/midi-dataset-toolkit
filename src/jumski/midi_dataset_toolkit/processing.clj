(ns jumski.midi-dataset-toolkit.processing
  (:require [jumski.midi-dataset-toolkit.midi :as midi]))

;; Helpers

(def ^:private only-midi-files
  "Transducer, filtering only paths to midi files, based on extension."
  (let [midifile-regex #"(?i).*\.midi?$"]
    (comp (filter #(.isFile %))
          (filter #(re-matches midifile-regex (.getPath %))))))

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

;; Quantization

(defn- quantize
  "Quantizes `s` value to the grid of width `qval`.
  If `s` is more than half of `qval` after the grid point, align it to next point.
  Otherwise, align it to previous point.
  Examples for `qval` of 100 (s before -> s after):
  50 => 100
  99 => 100
  150 => 200
  149 => 100
  49 => 0
  100 => 100"
  [qval s]
  (let [offset (int (/ qval 2))]
    (-> s
        (+ offset)
        (/ qval)
        int
        (* qval))))

;; Track-transforming functions

(defn quantize-track [track]
  "Quantizes all `note-ons` :timestamp for given track."
  (let [q-fn #(quantize 100 %)
        note-on-fn #(update % :timestamp q-fn)]
    (update track :note-ons (partial map note-on-fn))))

(defn steppize-track
  "Groups all notes with same :timestamp together."
  [{:keys [index note-ons path]}]
  (let [grouped (group-by :timestamp note-ons)
        sorted (sort-by first grouped)]
    {:index index
     :path path
     :steps (for [[_ notes] sorted] (map :note notes))}))

(defn binarize-track
  "Converts groups of notes to 128-char-long string of 0s and 1s,
  that will be fed to neural network. String is a binary number, little-endian
  encoded (least significant byte on the right). Each byte represents a note
  number that plays for given step."
  [{:keys [index steps path]}]
  (let [bitsteps (map notes-to-bitmask steps)
        bitstrings (map bitmask-to-bitstring bitsteps)]
    {:index index
     :path path
     :stepstring (clojure.string/join "\n" bitstrings)}))

;; Main transducer

(def midifile->stepstrings
  "Filters midi files, loads all tracks from them, quantizes their notes
  and converts to stepstrings of 0s and 1s, where one line represets
  one step of quantized notes from a track."
  (comp only-midi-files
        (mapcat midi/load-tracks)
        (map quantize-track)
        (map steppize-track)
        (map binarize-track)))

