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


;; NEW IDEAS FOR PROCESSING PIPELINE

; midifile -> midi file on disk
; midi -> midi data structure created by loading midifile
; track -> single stream of events from given, midi has mutliple tracks
; step -> collection of notes that are marked as playing
; stepfile -> file containing all steps converted to binary representation
(comment

;; ??????
(defn track->binsteps [{index :index path :path notes :notes}]
  {:path (batch/steps-file-path path index)
   :steps (->> notes
               midi/notes-to-bitmask
               midi/bitmask-to-bitstring)})

;; working
(def only-note-ons
  "Transducer, filters only note-on? events and simplifies them."
  (comp
    (filter midi/note-on?)
    (map #(select-keys % [:timestamp :note]))))

;; working
(defn load-tracks
  "Returns sequence of Track's for given midifile at path
  Skips any tracks that does not have any note-ons."
  [midifile]
  (let [path (.getPath midifile)
        {tracks :tracks} (overtone.midi.file/midi-file path)]
    (for [[index {events :events}] (map-indexed list tracks)
          :when (some midi/note-on? events)]
      {:index index :path path :note-ons (sequence only-note-ons events)})))

;; working
(def only-midi-files
  "Transducer, filtering only paths to midi files, based on extension."
  (let [midifile-regex #"(?i).*\.midi?$"]
    (comp (filter #(.isFile %))
          (filter #(re-matches midifile-regex (.getPath %))))))

;; working
(defn files-in-dir-seq
  "Returns lazy sequence of midi files in given `dir`."
  [dir]
  (->> (clojure.java.io/file "resources/")
       file-seq))

;; working
(defn quantize-track [track]
  (let [q-fn #(quantization/quantize 100 %)
        note-on-fn #(update % :timestamp q-fn)]
    (update track :note-ons (partial map note-on-fn))))

;; working
(def process-files
  (comp only-midi-files
        (mapcat load-tracks)
        (map quantize-track)
        (map steppize-track)
        (map binarize-track)))

; {33100 [{:timestamp 33100, :note 72} {:timestamp 33100, :note 69} {:timestamp 33100, :note 77}],
;  27400 [{:timestamp 27400, :note 72} {:timestamp 27400, :note 69} {:timestamp 27400, :note 77}],
;    200 [{:timestamp 31200, :note 72} {:timestamp 31200, :note 69} {:timestamp 31200, :note 77}]}

(defn steppize-track
  [{:keys [index note-ons path]}]
  (let [grouped (group-by :timestamp note-ons)
        sorted (sort-by first grouped)]
    {:index index
     :path path
     :steps (for [[_ notes] sorted] (map :note notes))}))

(defn binarize-track
  [{:keys [index steps path]}]
  (let [bitsteps (map midi/notes-to-bitmask steps)
        bitstrings (map midi/bitmask-to-bitstring bitsteps)]
    {:index index
     :path path
     :stepstring (clojure.string/join "\n" bitstrings)}))

(defn spit-binarized-track!
  [{:keys [index path stepstring]}]
  (let [dst-path (batch/steps-file-path path index)]
    (spit dst-path stepstring)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(->> (files-in-dir-seq "resources/")
     (sequence process-files)
     first)

(doseq [track (sequence process-files (files-in-dir-seq "resources/"))]
  (spit-binarized-track! track))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

) ;(comment)
