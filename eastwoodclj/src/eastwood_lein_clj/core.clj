(ns eastwood-lein-clj.core
  (:require [clojure.core.async :refer :all]))

(defn main
  "I don't do a whole lot."
  []
  (println "Hello, World!"))

;;;;;;;;;;;;;;


(def trade-ch (chan))

(go-loop []
         (<! (timeout 1000))
         (print (<! trade-ch))
         (recur))

(go 
 (let [timeout-ch (timeout 1000)
       trade 100]
   (->
    (alt!
     [[trade-ch trade]] :sent
     timeout-ch :timed-out)
    print)))

;; currently, eastwood reports error with valid uses of alt!
;; reported this issue to jonase/eastwood issue page.
