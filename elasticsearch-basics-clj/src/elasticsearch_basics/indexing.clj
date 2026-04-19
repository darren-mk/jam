(ns elasticsearch-basics.indexing
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [elasticsearch-basics.helper :refer [url as-json index]]))

;; 1. 클러스터 상태 확인
(defn cluster-info []
  (:body (http/get (url "/"))))

(comment
  (json/decode (cluster-info) true)
  {:name "94c691818aac",
   :cluster_name "docker-cluster",
   :cluster_uuid "g84kqmpkQD-ziUFs533D8w",
   :version {:number "8.13.4",
             :build_date "2024-05-06T22:04:45.107454559Z",
             :lucene_version "9.10.0",
             :minimum_wire_compatibility_version "7.17.0",
             :build_flavor "default",
             :build_hash "da95df118650b55a500dcc181889ac35c6d8da7c",
             :build_snapshot false,
             :build_type "docker",
             :minimum_index_compatibility_version "7.0.0"},
   :tagline "You Know, for Search"})

;; 2. 인덱스 생성 / 삭제
(defn create-index! []
  (let [payload {:mappings
                 {:properties
                  {:title {:type "text"}
                   :author {:type "keyword"}
                   :year {:type "integer"}
                   :content {:type "text"}}}}]
    (:body (http/put (url "/" index)
                     (as-json payload)))))

(comment
  (create-index!)
  {:acknowledged true
   :shards_acknowledged true
   :index "books"})

(defn delete-index! []
  (:body (http/delete (url "/" index))))

(defn index-exists? []
  (try
    (http/head (url "/" index))
    true
    (catch Exception _ false)))

(comment
  (index-exists?)
  true
  (delete-index!)
  "{\"acknowledged\":true}"
  (index-exists?)
  false)
