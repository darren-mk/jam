(ns typedupclj.core
  (:require
   [malli.core :as m]
   [typed.clojure :as t]))

(defn pass [x]
  x)
(m/=> pass [:=> [:cat :int] :int])

(def address
   [:map
     [:street string?]
     [:city string?]
     [:zip int?]])

(defn insert [y]
  (if (odd? y)
    (pass y)
    (pass 1)))
(m/=> insert [:=> [:cat :int] :int])

(defn get-street [a]
  (:street a))
(m/=> get-street [:=> [:cat address] :string])

(get-street {:street "abc"
             :city "def"
             :zip 12345})

(comment
  (time
   (t/check-ns-clj 'typedupclj.core)))
