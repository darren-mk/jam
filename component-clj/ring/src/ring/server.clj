(ns ring.server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as j]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello Clojure, Hello Ring!"})

(defrecord Server [server-options server]
  component/Lifecycle
  (start [this]
    (let [server (j/run-jetty
                  handler server-options)]
      (assoc this :server server)))
  (stop [this]
    (when server (.stop server))  
    (assoc this :server nil)))
