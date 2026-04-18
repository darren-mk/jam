(ns mongerupclj.indexes
  (:require [monger.collection :as mc]
            [monger.operators :refer [$gt $text $search]]
            [monger.core :as mg]
            [mongerupclj.db :refer [db]]))

;; ── 인덱스란? ─────────────────────────────────────────────────────────────────
;; 인덱스가 없으면 MongoDB 는 쿼리마다 컬렉션 전체를 순차 탐색합니다 (Collection Scan)
;; 인덱스가 있으면 트리 구조로 빠르게 찾습니다 (Index Scan)
;;
;; _id 필드는 자동으로 인덱스가 생성됩니다

;; ── 데이터 준비 ───────────────────────────────────────────────────────────────

(mc/remove db :products {})

(mc/insert-batch db :products=--009-
                 (mapv (fn [i] {:name (str "product-" i)
                                :category (rand-nth ["electronics" "food" "clothing"])
                                :price (+ 10 (rand-int 990))
                                :stock (rand-int 100)})
                       (range 100000)))

;; ── 인덱스 없이 쿼리 (Collection Scan) ──────────────────────────────────────
;; explain 으로 실행 계획 확인
;; "COLLSCAN" → 전체 순차 탐색

(time
 (mc/find-maps db :products {:category "electronics"}))
;; 인덱스 없으면 10만 개를 모두 확인

;; ── 단일 필드 인덱스 ─────────────────────────────────────────────────────────
;; mc/ensure-index → 없으면 만들고, 있으면 그냥 넘어감
;;
;; 두 번째 인자: {필드 방향}
;; 1  → 오름차순
;; -1 → 내림차순 (범위 쿼리 방향과 맞추면 더 빠름)

(mc/ensure-index db :products  {:category 1})

(time
 (mc/find-maps db :products {:category "electronics"}))
;; 인덱스 사용 → 훨씬 빠름

;; ── 복합 인덱스 (Compound Index) ─────────────────────────────────────────────
;; 여러 필드를 묶어서 하나의 인덱스로 만듦
;; 순서가 중요합니다: 앞 필드가 더 선택적(selective) 이어야 효율적

(mc/ensure-index db :products {:category 1, :price -1})

;; category + price 를 함께 쓰는 쿼리에서 효과적
(mc/find-maps db :products {:category "food" :price {$gt 500}})

;; 복합 인덱스는 앞 필드만으로도 사용 가능 (prefix rule)
;; :category 단독 쿼리 → 위 복합 인덱스 활용 가능
;; :price 단독 쿼리   → 복합 인덱스 활용 불가 (앞 필드가 없으므로)

;; ── 유니크 인덱스 ────────────────────────────────────────────────────────────
;; :unique true → 중복 값 삽입 시 오류 발생

(mc/ensure-index db :products (array-map :name 1) {:unique true})

;; 중복 삽입 시도 → 오류 발생
(try
  (mc/insert db :products {:name "product-1" :category "food" :price 100 :stock 10})
  (catch Exception e
    (println "중복 오류:" (.getMessage e))))

;; ── 텍스트 인덱스 ────────────────────────────────────────────────────────────
;; 문자열 필드에 대한 전문 검색(full-text search)
;; 컬렉션당 텍스트 인덱스는 1개만 가능

(mc/remove db :articles {})

(mc/insert-batch db :articles
                 [{:title "Clojure is a functional language" :body "It runs on JVM"}
                  {:title "MongoDB is a document database"   :body "It stores JSON-like data"}
                  {:title "Learning Clojure and MongoDB"      :body "Great combination"}])

(mc/ensure-index db :articles (array-map :title "text" :body "text"))

;; $text + $search 로 전문 검색
(mc/find-maps db :articles {$text {$search "Clojure"}})
;; → "Clojure" 가 포함된 문서

(mc/find-maps db :articles {$text {$search "Clojure MongoDB"}})
;; → "Clojure" 또는 "MongoDB" 가 포함된 문서 (OR 검색)

(mc/find-maps db :articles {$text {$search "\"document database\""}})
;; → 정확한 구문 검색 (따옴표로 감쌈)

;; ── 인덱스 목록 확인 ─────────────────────────────────────────────────────────

(mc/indexes-on db :products)
(mc/indexes-on db :articles)

;; ── 인덱스 삭제 ──────────────────────────────────────────────────────────────

;; 특정 인덱스 삭제
(mc/drop-index db :products (array-map :category 1))

;; _id 를 제외한 모든 인덱스 삭제
(mc/drop-all-indexes db :products)
