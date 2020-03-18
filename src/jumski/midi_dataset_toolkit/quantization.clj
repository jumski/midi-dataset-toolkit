(ns jumski.midi-dataset-toolkit.quantization)

;;;; QUANTIZATION
(defn quantize
  "Aligns `s` to `qval`-wide grid.
  Used to group steps that are close to each other to appear as a single step.
  Given `qval` of 4, those will be values returned for given `s`:
  0 -> 0
  2 -> 2
  3 -> 4
  5 -> 4
  6 -> 4
  7 -> 8"
  [qval s]
  (let [offset (int (/ qval 2))]
    (-> s
        (+ offset)
        (/ qval)
        int
        (* qval))))

(comment
  (let [steps (range 16)
        qval 4
        exp   [0 0 4 4 4 4 8 8 8 8 12 12 12 12 16 16]
        qtzd (map (partial quantize qval) steps)]
    {:original steps
    :expected exp
    :quantized qtzd})

  (let [steps (map (partial * 1000) (range 16))]
    (map (partial qfn 4) steps))
  )
