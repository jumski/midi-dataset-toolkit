(ns jumski.midi-dataset-toolkit.quantization)

;;;; QUANTIZATION
(defn quantize
  "Quantizes `s` value to the grid of width `qval`.
  If `s` is more than half of `qval` after the grid point, align it to next point.
  Otherwise, align it to previous point.
  Examples for `qval` of 100 (s before -> s after):
  50 => 100
  99 => 100
  150 => 200
  149 => 100
  49 => 0
  100 => 100"
  [qval s]
  (let [offset (int (/ qval 2))]
    (-> s
        (+ offset)
        (/ qval)
        int
        (* qval))))
