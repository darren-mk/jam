(ns user
  (:require
   [laciniaclj.schema :as s]
   [com.walmartlabs.lacinia :as l]
   [clojure.walk :as w])
  (:import (clojure.lang IPersistentMap)))

(def schema
  (s/load-schema))

(defn simplify
  "converts all ordered maps nested
  within the map into standard hash maps,
  and sequences into vecotrs,
  which makes for easier constants in the tests,
  and eliminates ordering problems."
  [m]
  (w/postwalk
   (fn [node]
     (cond
       (instance? IPersistentMap node) (into {} node)
       (seq? node) (vec node)
       :else node))
   m))

(defn q
  [query-string]
  (l/execute schema query-string nil nil))
