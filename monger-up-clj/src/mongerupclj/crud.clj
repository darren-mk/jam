(ns mongerupclj.crud
  (:require [monger.collection :as mc]
            [monger.operators :refer [$set]]
            [mongerupclj.db :refer [db]]))

;; ── 데이터 준비 ───────────────────────────────────────────────────────────────

(mc/remove db :users {})

(mc/insert db :users
           {:name "Alice" :age 30 :city "Seoul"})

(mc/insert-batch db :users
                 [{:name "Bob"     :age 25 :city "Busan"}
                  {:name "Charlie" :age 35 :city "Seoul"}
                  {:name "Dana"    :age 28 :city "Incheon"}
                  {:name "Eve"     :age 22 :city "Seoul"}])


;; ── INSERT ───────────────────────────────────────────────────────────────────
;; mc/insert      → 문서 1개 삽입, 삽입된 문서(with :_id) 반환
;; mc/insert-batch → 여러 문서 한번에 삽입

(def alice (mc/insert db :users {:name "Alice" :age 30 :city "Seoul"}))
(:_id alice) ;; → ObjectId, 나중에 update-by-id 에 활용


;; ── FIND ─────────────────────────────────────────────────────────────────────
;; mc/find-maps       → 조건에 맞는 문서 전체를 벡터로 반환
;; mc/find-one-as-map → 조건에 맞는 첫 번째 문서만 반환

(mc/find-maps db :users {})
(mc/find-maps db :users {:city "Seoul"})
(mc/find-one-as-map db :users {:name "Alice"})


;; ── UPDATE ───────────────────────────────────────────────────────────────────
;; mc/update       → 조건으로 찾아 첫 번째 문서 수정
;; mc/update-by-id → _id 로 찾아 수정 (더 명확하고 빠름)
;;
;; $set 없이 update 하면 문서 전체가 교체됩니다!

(mc/update db :users {:name "Alice"} {$set {:city "Daegu"}})
(mc/update-by-id db :users (:_id alice) {$set {:city "Busan"}})

(mc/find-one-as-map db :users {:name "Alice"})


;; ── DELETE ───────────────────────────────────────────────────────────────────
;; mc/remove       → 조건에 맞는 모든 문서 삭제
;; mc/remove-by-id → _id 로 특정 문서 삭제
;;
;; 주의: (mc/remove db :users {}) 는 전체 삭제!

(mc/remove db :users {:name "Eve"})
(mc/find-maps db :users {})


;; ── COUNT ────────────────────────────────────────────────────────────────────

(mc/count db :users {})
(mc/count db :users {:city "Seoul"})
