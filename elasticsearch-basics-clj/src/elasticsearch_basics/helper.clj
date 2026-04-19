(ns elasticsearch-basics.helper
  (:require [cheshire.core :as json]))

(def base-url "http://localhost:9200")
(def index "books")

(defn url [& parts]
  (apply str base-url parts))

(defn as-json [body]
  {:content-type :json
   :accept :json
   :as :json
   :body (json/generate-string body)})
