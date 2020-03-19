(ns jumski.midi-dataset-toolkit.batch
  (:require [overtone.midi.file :as midifile]
            [jumski.midi-dataset-toolkit.midi :as midi]))

;; PRIVATE

(def ^:private midifile-regex #"(?i).*\.midi?$")

(defn- find-midis
  "Returns `file-seq` of all `*.mid{,i}` files in given `dir`."
  [dir]
  (let [dir (clojure.java.io/file dir)]
    (->> (file-seq dir)
         (filter #(.isFile %))
         (filter #(re-matches midifile-regex (.getPath %))))))

(defn steps-file-path
  "Returns path like midipath but with `.steps` extension and added idx as suffix."
  [midipath idx]
  (str midipath ".part_" (format "%02d" idx) ".steps"))

;; PUBLIC

(defn midifile->steps-files!
  "Saves each track from midifile as separate steps file.
  Each step file will be saved in same directory that original midi file,
  will have same name as original midi file but with numeric suffix
  in form of track number (`_1`) and extension changed to `.steps`."
  [path]
  (let [idx-steps (->> (midifile/midi-file path)
                       (midi/note-on-tracks)
                       (map :events)
                       (map #(filter midi/note-on? %))
                       (map midi/events->steps)
                       (map-indexed list))]
    (doseq [[idx steps] idx-steps
            :let [opath (steps-file-path path idx)]]
      (spit opath steps))))

(defn mididir->steps-files!
  [dir]
  (doseq [midifile (find-midis dir)
          :let [midipath (.getPath midifile)]]
    (println (str "Converting " midipath))
    (midifile->steps-files! midipath)))

