(ns jumski.midi-dataset-toolkit.quantization-test
  (:require [midje.sweet :refer :all]
            [jumski.midi-dataset-toolkit.quantization :as q]))

(facts
  "quantize aligns stuff to a grid"

  (facts
    "aligns to next grid point if less than grid point by half qval or more"
    (q/quantize 100 50) => 100
    (q/quantize 100 99) => 100
    (q/quantize 100 150) => 200)

  (facts
    "aligns to previous grid point if more than grid point by less than half qval"
    (q/quantize 100 149) => 100
    (q/quantize 100 49) => 0)

  (facts
    "does not change if lies exacly on grid point"
    (q/quantize 100 100) => 100))






