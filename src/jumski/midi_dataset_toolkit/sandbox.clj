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


;; NEW IDEAS FOR PROCESSIGN PIPELINE

; midifile -> midi file on disk
; midi -> midi data structure created by loading midifile
; track -> single stream of events from given, midi has mutliple tracks
; step -> collection of notes that are marked as playing
; stepfile -> file containing all steps converted to binary representation
(comment

; (defrecord Track [path events])

; (defn write-stepfile! [{path :path steps :steps}]
;   (->> steps


(defn track->binsteps [{idx :idx path :path notes :notes}]
  {:path (batch/steps-file-path path idx)
   :steps (->> notes
               midi/notes-to-bitmask
               midi/bitmask-to-bitstring)})

(defn load-tracks
  "Returns sequence of Track's for given midifile at path
  Skips any tracks that does not have any note-ons."
  [path]
  (let [{tracks :tracks} (overtone.midi.file/midi-file path)]
    (for [[idx {events :events}] (map-indexed list tracks)
          :when (some midi/note-on? events)
          :let [note-ons (->> events
                             (filter midi/note-on?)
                             (map #(select-keys % [:timestamp :note])))]]
      {:idx idx :path path :note-ons note-ons})))

;; working
(def only-midi-paths
  "Transducer, filtering only paths to midi files, based on extension."
  (let [midifile-regex #"(?i).*\.midi?$"]
    (comp (filter #(.isFile %))
          (filter #(re-matches midifile-regex (.getPath %)))
          (map #(.getPath %)))))

;; working
(defn midi-paths-seq
  "Returns lazy sequence of midi paths (strings) in given `dir`."
  [dir]
  (->> (clojure.java.io/file "resources/")
       file-seq
       (sequence only-midi-paths)))

; (defn quantize-track [{note-ons :note-ons :as track}]
;   (let [quantized (->> note-ons
;                        (map (juxt :timestamp :note))
;                        (group-by #(quantization/quantize 100 (first %)))
;                        (sort-by first))
;         simplified (for [[_ [_ [_ note]]] quantized] note)]
;     simplified))

(->> (midi-paths-seq "resources/")
     (mapcat load-tracks)
     first
     quantize-track

    ; (map track->stepfile)
    ; (map write-stepfile!))
     )

(defn quantize-track [track]
  (let [q-fn #(quantization/quantize 100 %)
        note-on-fn #(update % :timestamp q-fn)]
    (update track :note-ons (partial map note-on-fn))))




) ;(comment)
