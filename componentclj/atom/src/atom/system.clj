(ns atom.system
  (:require [com.stuartsierra.component :as component]
            [atom.person :as person]))

(defn make-system
  [configs]
  (component/system-map
   :age
   (person/->Age (:starting-age configs))
   :nutrition-level
   (person/->NutritionLevel
    (:starting-nutrition-level configs))))

