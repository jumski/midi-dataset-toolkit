(ns jumski.midi-dataset-toolkit.batch
  (:require [overtone.midi.file :as midifile]
            [jumski.midi-dataset-toolkit.midi :as midi]))

(def MIDIFILE_REGEX #"(?i).*\.midi?$")

(defn- find-midis
  "Returns `file-seq` of all `*.mid{,i}` files in given `dir`."
  [dir]
  (let [dir (clojure.java.io/file dir)]
    (->> (file-seq dir)
         (filter #(.isFile %))
         (filter #(re-matches MIDIFILE_REGEX (.getPath %))))))

(defn steps-file-path
  "Returns path like midipath but with `.steps` extension and added idx as suffix."
  [midipath idx]
  (str midipath
       (str ".part_" (format "%02d" idx))
       ".steps"))

(defn midifile->steps-files!
  "Saves each track from midifile as separate steps file.
  Each step file will be saved in same directory that original midi file,
  will have same name as original midi file but with numeric suffix
  in form of track number (`_1`) and extension changed to `.steps`."
  [path]
  (let [idx-steps (->> (midifile/midi-file path)
                       (midi/note-on-tracks)
                       (map :events)
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

(comment
  (def dub_midis "/home/jumski/Documents/midis/Dub_MIDIRip")
  (def c_major_scale "resources/c_major_scale.mid")

  (midifile->steps-files! c_major_scale)

  (doseq [midifile (find-midis dub_midis)
          :let [midipath (.getPath midifile)]]
    (println (str "Converting " midipath))
    (midifile->steps-files! midipath))
)

