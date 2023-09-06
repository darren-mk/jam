(ns typedupclj.core
  (:require
   [malli.core :as m]
   [typed.clojure :as t]))

(def Address
  [:map
   [:address/street string?]
   [:address/city string?]
   [:address/zip int?]])

(defn step-up-zip [address]
  (update address :address/zip inc))

(m/=> step-up-zip
  [:=> [:cat Address] Address])

(step-up-zip {:address/city "Totowa"
              :address/street "First 123"
              :address/zip 12345})

(comment
  (require '[malli.instrument :as mi])
  (mi/instrument!))


#_
(t/cns 'typedupclj.core)







#_#_
(defn pass [x y]
  (if y
    (* x 2)
    (/ x 2)))
(m/=> pass [:=> [:cat :int] :int])

#_
(defn get-street [a]
  (if (> (:zip a) 5)
    (update a :zip inc)
    (update a :zip dec)))

#_
(m/=> get-street [:=> [:cat model/address]
                  model/address])



#_
(m/=> step-up [:=> [:cat Address] Address])


#_
(defn buzz [n]
  (* n 2))
#_(m/=> buzz [:=> [:cat [:and :int [:> 4]]]
            [:and :int [:< 21]]])
#_#_
(defn fuzz [n]
  (* n 3))
(m/=> fuzz [:=> [:cat [:and :int [:> 29]]] [:and :int [:> 98]]])

#_
(def i
  (-> 6 buzz fuzz))
