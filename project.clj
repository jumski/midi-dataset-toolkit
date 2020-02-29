(defproject midi-dataset-toolkit "0.1.0-SNAPSHOT"
  :description "Toolkit for reading midi files and processing them to create a dataset of musical steps"
  :url "https://github.com/jumski/midi-dataset-toolkit"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [overtone "0.10.6"]]
  :repl-options {:init-ns midi-dataset-toolkit.core}
  :main midi-dataset-toolkit.core
  :plugins [[lein-bin "0.3.4"]]
  :profiles {:uberjar {:aot [midi-dataset-toolkit.core]}}
  :bin {:name "midi2stepfile"
        :bin-path "./bin"
        :bootclasspath false})
