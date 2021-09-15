(ns interview.core)

;; (def level (atom 1))
;; (def direction (atom :up))

(def floors
  (zipmap (iterate inc 0)       
          [-2 -1 0 1 2 3 5]))

(defn get-floor-index [current-floor]
  (key (first (filter
               (fn [x]
                 (= current-floor (val x)))
               floors))))

(defn move [current-floor direction]
  (let [current-index (get-floor-index current-floor)
        f (case direction
             :up inc
             :down dec
             identity)
        calc-index (f current-index)
        lowest-index (apply min (keys floors))
        highest-index (apply max (keys floors))]      
    (cond (< calc-index lowest-index) (get floors lowest-index)
          (> calc-index highest-index) (get floors highest-index)
          :default (get floors calc-index))))

(defn abs [x]
  (if (< x 0)
    (* x -1)
    x))

(defn single-plan [current-floor target-floor]
  (let [current-index (get-floor-index current-floor)
        target-index (get-floor-index target-floor)
        move-direction (if (< current-index target-index)
                         :up :down)
        repetition (abs (- target-index current-index))]
    (into [] (concat
              (for [_ (range repetition)]
                move-direction)
              [:open :close]))))

(defn create-plans [current-floor target-floors]
  (let [full (into [] (flatten [current-floor target-floors]))
        args  (for [i (-> full count dec range)]
                (vector (get full i)
                        (get full (inc i))))
        plans (for [arg args]
                (apply single-plan arg))]  
    (into [] (apply concat plans))))

(comment 
  "quick repl testing on developments."
  (create-plans 1 [3 5])
  (concat [1 2 3] [4 5])
  (apply single-plan [1 3])
  (flatten [1 [3 5]]) ;; => (1 3 5)
  (for [i (range (dec (count [1 3 5])))]
    (vector (get [1 3 5] i)
            (get [1 3 5] (inc i)))
    ) ;; => ([1 3] [3 5])
  (concat
   (single-plan 1 3)
   (single-plan 3 5)))
