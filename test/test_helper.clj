(ns test-helper
  (:require [clojure.java.io]))

(defn- find-files-by-pattern
  "Returns `file-seq` of all files with names matching pattern."
  [dir patt]
  (let [dir (clojure.java.io/file dir)]
    (->> (file-seq dir)
         (filter #(.isFile %))
         (filter #(re-matches patt (.getPath %))))))

(def ^:private fixtures-root "test/resources")

;; PUBLIC FNS

(defn fixture-path
  "Joins parts by /, prefixing by `fixtures-root`.
  Symbols are converted to their names."
  [& parts]
  (let [parts (map name parts)]
    (clojure.string/join "/" (cons fixtures-root parts))))

(defn clean-fixture-dir
  "Removes all *.steps files in dir."
  [dirname]
  (let [dir (fixture-path dirname)
        patt #"(?i).*\.steps$"]
    (doseq [f (find-files-by-pattern dir patt)]
      (println "deleting" (.getPath f))
      (.delete f))))

