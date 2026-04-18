(ns mongerupclj.query-operators
  (:require [monger.collection :as mc]
            [monger.operators :refer [$in $nin $gt $lt $gte $lte $ne
                                      $or $and $exists $regex]]
            [mongerupclj.db :refer [db]]))

;; ── 데이터 준비 ───────────────────────────────────────────────────────────────

(mc/remove db :users {})

(mc/insert-batch db :users
                 [{:name "Alice"   :age 30 :city "Seoul"   :score 88}
                  {:name "Bob"     :age 25 :city "Busan"   :score 72}
                  {:name "Charlie" :age 35 :city "Seoul"   :score 95}
                  {:name "Dana"    :age 28 :city "Incheon" :score 60}
                  {:name "Eve"     :age 22 :city "Seoul"   :score 78}
                  {:name "Frank"   :age 40 :city "Daegu"   :score 55}])


;; ── 비교 연산자 ───────────────────────────────────────────────────────────────
;; 핵심 문법: {:필드 {$연산자 값}}

(mc/find-maps db :users {:age {$gt 30}})          ;; 30 초과 → Charlie, Frank
(mc/find-maps db :users {:age {$lte 28}})         ;; 28 이하 → Bob, Dana, Eve
(mc/find-maps db :users {:score {$gte 60 $lte 88}}) ;; 60~88 → Alice, Bob, Dana, Eve
(mc/find-maps db :users {:city {$ne "Seoul"}})    ;; Seoul 아님 → Bob, Dana, Frank


;; ── 배열 연산자 ───────────────────────────────────────────────────────────────

(mc/find-maps db :users {:city {$in ["Seoul" "Busan"]}})  ;; → Alice, Bob, Charlie, Eve
(mc/find-maps db :users {:city {$nin ["Seoul" "Busan"]}}) ;; → Dana, Frank


;; ── 논리 연산자 ───────────────────────────────────────────────────────────────
;; 주의: 같은 필드 조건 두 개는 {:age {$gt 25 $lt 35}} 처럼 한 map 안에!
;;       {:age {$gt 25} :age {$lt 35}} 는 Clojure map 키 중복으로 안 됨

(mc/find-maps db :users {$or [{:age {$lt 25}} {:score {$gte 90}}]})
;; → Eve (age 22), Charlie (score 95)

(mc/find-maps db :users {:city "Seoul" :score {$gte 80}})
;; → Alice (88), Charlie (95)


;; ── 존재 여부 ─────────────────────────────────────────────────────────────────
;; 스키마가 자유로운 MongoDB 에서 자주 씁니다

(mc/find-maps db :users {:score {$exists true}})
(mc/find-maps db :users {:score {$exists false}})


;; ── 정규식 ────────────────────────────────────────────────────────────────────

(mc/find-maps db :users {:name {$regex "e$"}})      ;; "e" 로 끝나는 이름 → Alice, Charlie
(mc/find-maps db :users {:name {$regex "(?i)a"}})   ;; "a/A" 포함 → Alice, Charlie, Dana, Frank
