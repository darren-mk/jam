(ns laciniaclj.schema-test
  (:require
   [clojure.test :as t]
   [clojure.java.io :as io]
   [clojure.walk :as walk]
   [com.walmartlabs.lacinia :as lac]
   [laciniaclj.schema :as schema]

   )
  (:import (clojure.lang IPersistentMap)))

(defn simplify
  [m]
  (walk/postwalk
   (fn [node]
     (cond
       (instance? IPersistentMap node) (into {} node)
       (seq? node) (vec node)
       :else node))
   m))

(def compiled-schema
  (schema/load-schema))

(lac/execute compiled-schema
             "{ game_by_id(id: 1234) { name summary min_player max_player play_time }}"
             nil nil)

(with-redefs [schema/resolve-game-by-id
              (fn [_ _ _ _]
                {:id "1234"
                 :name "ZertzDDD"
                 :summary "Two player abstract with forced moves and shrinking board"
                 :min_players 2
                 :max_players 2})]
  (lac/execute
   (schema/load-schema)
   "{ game_by_id(id: 1234) { name }}"
   nil nil))



