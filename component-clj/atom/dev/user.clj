(ns user
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer (refresh)]
            [atom.system :as app]))

(def configs
  {:starting-age 8
   :starting-nutrition-level 5})

(def system nil)

(defn init []
  (alter-var-root
   #'system
   (constantly (app/make-system configs))))

(defn start []
  (alter-var-root
   #'system
   component/start))

(defn stop []
  (alter-var-root
   #'system
   (fn [s] (when s component/stop))))

(defn go []
  (do (init) (start)))

(defn reset []
  (do (stop) (refresh :after 'user/go)))
