(ns atom.person
  (:require [com.stuartsierra.component :as component]))

(defrecord Age [starting-age]
  component/Lifecycle
  (start [this]
    (assoc this :age (atom starting-age)))
  (stop [this]
    (assoc this :age nil)))

(defrecord NutritionLevel [starting-nutrition-level]
  component/Lifecycle
  (start [this]
    (assoc this :nutrition-level
           (atom starting-nutrition-level)))
  (stop [this]
    (assoc this :nutrition-level nil)))
