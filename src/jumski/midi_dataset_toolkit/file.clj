(ns jumski.midi-dataset-toolkit.file
  (:require [jumski.midi-dataset-toolkit.processing :as processing]))

;; Helpers

(defn- files-in-dir-seq
  "Returns lazy sequence of java.io.File in given `dir`."
  [dir]
  (->> (clojure.java.io/file dir)
       file-seq))

(defn- steps-file-path
  "Returns path like path but with `.steps` extension and added idx as suffix."
  [path idx]
  (str path ".part_" (format "%02d" idx) ".steps"))

(defn- spit-binarized-track!
  "Writes stepstring for given track to disk, at path built by suffixing midi path
  with track index."
  [{:keys [index path stepstring]}]
  (let [dst-path (steps-file-path path index)]
    (spit dst-path stepstring)))

;; Public

(defn process-all-midi-files-in-dir!
  "Finds all midi files, extract note-on tracks from them,
  quantizes each track and writes to suffixed file as stepstring."
  ([dir] (process-all-midi-files-in-dir! dir (fn [track])))
  ([dir progress-fn]
  (doseq [track (sequence processing/midifile->stepstrings (files-in-dir-seq dir))]
    (progress-fn track)
    (spit-binarized-track! track))))
