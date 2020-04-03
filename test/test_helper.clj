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

(defn load-fixture
  "Slurps file from path build by `fixture-path`."
  [& path-parts]
  (-> (slurp (apply fixture-path path-parts))
      clojure.string/trim))

(defn load-trimmed-fixture
  "Loads fixture via `load-fixture` and trims whitespaces from both ends."
  [& path-parts]
  (-> (apply load-fixture path-parts)
      clojure.string/trim))

(defn clean-fixture-dir!
  "Removes all *.steps files in dir."
  [dirname]
  (let [dir (fixture-path dirname)
        patt #"(?i).*\.steps$"]
    (doseq [f (find-files-by-pattern dir patt)]
      (.delete f))))

