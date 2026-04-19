(ns elasticsearch-basics.document
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [elasticsearch-basics.helper :refer [url as-json index]]))

(defn index-doc! [id doc]
  (:body (http/put (url "/" index "/_doc/" id)
                   (as-json doc))))

(comment
  (index-doc! "1" {:title "Clean Code"
                   :author "Robert Martin"
                   :year 2008
                   :content "소프트웨어 장인 정신"})
  (index-doc! "2" {:title "The Pragmatic Programmer"
                   :author "David Thomas"
                   :year 1999
                   :content "writing good code and practical programming"})
  (index-doc! "3" {:title "SICP"
                   :author "Harold Abelson"
                   :year 1984
                   :content "code as data, data as code"})
  ;; bool 쿼리 실험용 추가 데이터
  ;; - 제목에 "code" O, 저자 X
  (index-doc! "4" {:title "Code Complete"
                   :author "Steve McConnell"
                   :year 2004
                   :content "소프트웨어 구현의 모든 것"})
  ;; - 제목에 "code" X, 저자 O
  (index-doc! "5" {:title "Clean Architecture"
                   :author "Robert Martin"
                   :year 2017
                   :content "소프트웨어 아키텍처의 원칙"}))

(defn get-doc [id]
  (:body (http/get (url "/" index "/_doc/" id))))

(comment
  (json/decode (get-doc "1") true)
  {:_index "books"
   :_id "1"
   :_version 1
   :_seq_no 0
   :_primary_term 1
   :found true
   :_source {:title "Clean Code"
             :author "Robert Martin"
             :year 2008
             :content "소프트웨어 장인 정신"}})

(defn update-doc! [id partial-doc]
  (:body (http/post (url "/" index "/_update/" id)
                    (as-json {:doc partial-doc}))))

(comment
  (update-doc! "1" {:year 2010})
  :=> {:_index "books",
       :_id "1",
       :_version 2,
       :result "updated",
       :_shards {:total 2, :successful 1, :failed 0},
       :_seq_no 7,
       :_primary_term 1}
  (json/decode (get-doc "1") true)
  :=> {:_index "books",
       :_id "1",
       :_version 2,
       :_seq_no 7,
       :_primary_term 1,
       :found true,
       :_source {:title "Clean Code",
                 :author "Robert Martin",
                 :year 2010,
                 :content "소프트웨어 장인 정신"}})

(defn delete-doc! [id]
  (:body (http/delete (url "/" index "/_doc/" id))))
