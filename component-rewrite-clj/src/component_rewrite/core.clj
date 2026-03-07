(ns component-rewrite.core
  (:require
   [com.stuartsierra.dependency :as dep]))

(defprotocol Lifecycle
  :extend-via-metadata true
  (start [component])
  (stop [component]))

(extend-protocol Lifecycle
  java.lang.Object
  (start [this] this)
  (stop [this] this))

(defn using [component dependencies]
  (let [dependencies'
        (cond (map? dependencies) dependencies
              (vector? dependencies) (reduce
                                      (fn [acc item]
                                        (assoc acc item item))
                                      {} dependencies)
              :else (throw (ex-info "dependencies must be map or vector" {})))]
    (vary-meta component
               update ::dependencies
               merge dependencies')))

(defn dependencies [component]
  (-> component meta
      (get ::dependencies)))

(comment
  (dependencies
   (using
    (using {:a 1} [:db])
    [:cache]))
  ;; => {:db :db, :cache :cache}
  ;; => {:b 2, :c 3}
  )

(defn get-component [system key]
  (let [component (get system key ::not-found)]
    (when (nil? component)
      (throw (ex-info "nil component" {:key key})))
    (when (= ::not-found component)
      (throw (ex-info "component not found" {:key key})))
    component))

(defn dependency-graph
  [system component-keys]
  (reduce-kv (fn [graph key component]
               (reduce #(dep/depend %1 key %2)
                       graph
                       (vals (dependencies component))))
             (dep/graph)
             (select-keys system component-keys)))

(defn assoc-dependencies [component system]
  (reduce-kv
   (fn [acc k v]
     (assoc acc k (get-component system v)))
   component (dependencies component)))
