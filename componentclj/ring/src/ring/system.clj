(ns ring.system
  (:require [com.stuartsierra.component :as component]
            [ring.server :as server]))

(defn make-system
  [configs]
  (component/system-map
   :server (server/map->Server configs)))
