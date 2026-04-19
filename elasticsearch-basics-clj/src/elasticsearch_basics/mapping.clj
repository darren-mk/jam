(ns elasticsearch-basics.mapping
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [elasticsearch-basics.helper :refer [url as-json index]]))

;; Mapping — 인덱스의 필드 타입 정의
;; 관계형 DB의 스키마와 비슷하지만 두 가지 제약이 있음:
;;   - 새 필드 추가는 가능
;;   - 기존 필드의 타입 변경은 불가 (변경하려면 새 인덱스를 만들고 reindex 해야 함)

;; 1. 현재 매핑 조회
(defn get-mapping []
  (:body (http/get (url "/" index "/_mapping"))))

(comment
  (json/decode (get-mapping) true)
  :=> {:books
       {:mappings
        {:properties
         {:author {:type "keyword"},
          :content {:type "text"},
          :title {:type "text"},
          :year {:type "integer"}}}}})

;; 2. Dynamic mapping — 명시하지 않은 필드는 ES가 자동으로 타입 추론
;; 문서를 인덱싱할 때 새 필드가 있으면 자동으로 매핑에 추가됨
;; 단점: 의도치 않은 타입으로 추론될 수 있음 (예: "2008" → text로 저장)

(comment
  ;; rating 필드를 새로 추가해서 인덱싱하면
  ;; ES가 자동으로 float 타입으로 매핑에 추가함
  (http/put (url "/" index "/_doc/99")
            (as-json {:title "Test" :author "Test" :year 2020
                      :content "테스트" :rating 4.5}))
  :=> {:body {:_index "books",
              :_id "99",
              :_version 1,
              :result "created",
              :_shards {:total 2, :successful 1, :failed 0},
              :_seq_no 19,
              :_primary_term 1}}

  ;; 매핑 다시 조회하면 rating: float 이 추가됨
  (json/decode (get-mapping) true)
  :=> {:books
       {:mappings
        {:properties
         {:author {:type "keyword"},
          :content {:type "text"},
          :rating {:type "float"},
          :title {:type "text"},
          :year {:type "integer"}}}}})

;; 3. 새 필드를 명시적으로 매핑에 추가
;; 기존 필드 타입 변경은 불가 — 새 필드 추가만 가능
(defn add-field! [field-name field-def]
  (:body (http/put (url "/" index "/_mapping")
                   (as-json {:properties {field-name field-def}}))))

(comment
  ;; tags 필드를 keyword 배열로 추가
  (add-field! :tags {:type "keyword"})
  :=> {:acknowledged true}

  (json/decode (get-mapping) true)
  :=> {:books
       {:mappings
        {:properties
         {:author {:type "keyword"},
          :content {:type "text"},
          :rating {:type "float"},
          :tags {:type "keyword"},
          :title {:type "text"},
          :year {:type "integer"}}}}}
  ;; 매핑에 tags: keyword 가 추가됨
  )

;; 4. 필드 타입 변경이 불가능한 이유
;; ES는 인덱싱 시점에 분석(tokenize)을 마치고 역색인(inverted index)을 저장함
;; 타입을 바꾸면 기존 역색인 구조 자체가 달라지므로 변경 불가
;;
;; 해결책: reindex
;; 1. 새 매핑으로 새 인덱스 생성
;; 2. _reindex API로 기존 → 새 인덱스로 데이터 복사
;; 3. alias를 새 인덱스로 전환

(comment
  ;; year 필드를 keyword로 바꾸려 하면 에러
  (add-field! :year {:type "keyword"})
  ;; => 400 Bad Request: mapper [year] cannot be changed from type [integer] to [keyword]
  )
