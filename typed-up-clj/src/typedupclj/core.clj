(ns typedupclj.core
  (:require
   [malli.core :as m]
   [typedupclj.model :as model]
   [typed.clojure :as t]))

(defn pass [x]
  x)
(m/=> pass [:=> [:cat :int] :int])

(defn hello [a]
  a)
(t/ann hello [t/Int :-> t/Int])

(defn insert [y]
  (if (odd? y)
    (pass y)
    (pass 1)))
(m/=> insert [:=> [:cat :int] :int])

(defn get-street [a]
  (:street a))
(m/=> get-street [:=> [:cat model/address] :string])

(comment
  (time
   (t/cns 'typedupclj.core)))
