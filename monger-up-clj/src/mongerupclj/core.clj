(ns mongerupclj.core
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId))

(def conn (mg/connect))

(def db (mg/get-db conn "mongerupclj"))

(def data
  (mapv (fn [i] {:name (str "name-" i) :age i}) (range 10000)))

(time
 (mc/insert-batch
  db "documents"
  data))
;; "Elapsed time: 108.427333 msecs"

(time
 (mc/find-maps db "documents" {:name "name-8888"}))
;; "Elapsed time: 0.341333 msecs"
;; "Elapsed time: 0.15325 msecs"
;; "Elapsed time: 0.139166 msecs"

(->
 (mc/find-maps db "documents" {:name "name-8888"})
 first
 :_id
 str)

(let [oid (ObjectId.)
      s (str oid)
      soid (ObjectId. s)]
  (= oid soid))
