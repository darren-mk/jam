(ns elasticsearch-basics.bulk
  (:require [clj-http.client :as http]
            [clojure.string :as cstr]
            [cheshire.core :as json]
            [elasticsearch-basics.helper :refer [url index]]))

;; Bulk API — 여러 문서를 한 번의 요청으로 처리
;; 요청 body는 줄바꿈으로 구분된 JSON 쌍으로 구성됨:
;;   {"index": {"_index": "books", "_id": "1"}}  <- 액션
;;   {"title": "...", "author": "..."}            <- 문서
;; 마지막 줄도 반드시 \n으로 끝나야 함

(defn- ->bulk-line [m]
  (json/generate-string m))

(defn bulk-index! [docs]
  ;; docs: [{:id "6" :doc {...}} ...]
  (let [body (->> docs
                  (mapcat (fn [{:keys [id doc]}]
                            [(->bulk-line {:index {:_index index :_id id}})
                             (->bulk-line doc)]))
                  (cstr/join "\n")
                  (#(str % "\n")))]
    (:body (http/post (url "/_bulk")
                      {:content-type "application/x-ndjson"
                       :accept :json
                       :as :json
                       :body body}))))

(comment
  ;; 3권을 한 번에 인덱싱
  (bulk-index! [{:id "6" :doc {:title "Refactoring"
                               :author "Martin Fowler"
                               :year 1999
                               :content "코드 개선의 기술"}}
                {:id "7" :doc {:title "Design Patterns"
                               :author "Gang of Four"
                               :year 1994
                               :content "객체지향 디자인 패턴"}}
                {:id "8" :doc {:title "Domain-Driven Design"
                               :author "Eric Evans"
                               :year 2003
                               :content "도메인 중심 설계"}}])
  :=> {:errors false, :took 6,
       :items [{:index {:_index "books",
                        :_id "6",
                        :_version 1,
                        :result "created",
                        :_shards {:total 2, :successful 1, :failed 0},
                        :_seq_no 16,
                        :_primary_term 1,
                        :status 201}}
               {:index {:_index "books",
                        :_id "7",
                        :_version 1,
                        :result "created",
                        :_shards {:total 2, :successful 1, :failed 0},
                        :_seq_no 17,
                        :_primary_term 1,
                        :status 201}}
               {:index {:_index "books",
                        :_id "8",
                        :_version 1,
                        :result "created",
                        :_shards {:total 2, :successful 1, :failed 0},
                        :_seq_no 18,
                        :_primary_term 1,
                        :status 201}}]}
  ;; 결과에서 errors: false 이면 전부 성공
  ;; items 배열에 각 문서의 처리 결과가 담김
  )
