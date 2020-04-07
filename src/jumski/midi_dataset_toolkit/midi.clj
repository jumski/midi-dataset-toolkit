(ns jumski.midi-dataset-toolkit.midi
  (:require [overtone.midi.file])
  (:import java.io.File))

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

(defn- print-error
  "Prints info about midifile path and error message catched."
  [^java.io.File midifile e]
  (let [msg (format "Error: loading '%s' raised error '%s' with message '%s'"
                    (.getPath midifile)
                    (str (class e))
                    (.getMessage ^java.lang.Exception e))]
    (println msg)))

; Public

(defn load-tracks
  "Returns sequence of Track's for given midifile at path
  Skips any tracks that does not have any note-ons.
  Logs errors to screen by default but accepts custom logger function."
  ([midifile] (load-tracks midifile print-error))
  ([^java.io.File midifile error-fn]
   (try
     (let [path (.getPath midifile)
           {tracks :tracks} (overtone.midi.file/midi-file path)]
       (for [[index {events :events}] (map-indexed list tracks)
             :when (some note-on? events)]
         {:index index :path path :note-ons (sequence only-note-ons events)}))
     (catch Exception e
       (do
         (error-fn midifile e)
         [{:error e}])))))

