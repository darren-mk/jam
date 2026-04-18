(ns mongerupclj.update-operators
  (:require [monger.collection :as mc]
            [monger.operators :refer [$set $unset $inc $push $pull $addToSet]]
            [mongerupclj.db :refer [db]]))

;; ── 데이터 준비 ───────────────────────────────────────────────────────────────

(mc/remove db :users {})

(mc/insert-batch db :users
                 [{:name "Alice"  :age 30 :city "Seoul"  :score 88 :tags ["clojure" "mongodb"]}
                  {:name "Bob"    :age 25 :city "Busan"  :score 72 :tags ["java"]}
                  {:name "Charlie" :age 35 :city "Seoul"  :score 95 :tags ["clojure" "haskell"]}])

;; ── $set ─────────────────────────────────────────────────────────────────────
;; 지정한 필드만 변경, 나머지 필드는 유지
;; 없는 필드를 $set 하면 새 필드가 추가됩니다

(mc/update db :users {:name "Alice"} {$set {:city "Daegu"}})
(mc/find-one-as-map db :users {:name "Alice"})
;; → :city "Daegu", 나머지 필드 유지

;; 없는 필드 추가
(mc/update db :users {:name "Bob"} {$set {:email "bob@email.com"}})
(mc/find-one-as-map db :users {:name "Bob"})
;; → :email 필드가 새로 생김

;; ── $unset ───────────────────────────────────────────────────────────────────
;; 필드를 문서에서 제거
;; 값은 무엇을 넣든 상관없음 (관례상 "" 또는 1 을 씁니다)

(mc/update db :users {:name "Bob"} {$unset {:email ""}})
(mc/find-one-as-map db :users {:name "Bob"})
;; → :email 필드가 사라짐

;; ── $inc ─────────────────────────────────────────────────────────────────────
;; 숫자 필드를 증가/감소
;; 양수 → 증가, 음수 → 감소

(mc/update db :users {:name "Alice"} {$inc {:score 5}})
(mc/find-one-as-map db :users {:name "Alice"})
;; → :score 93 (88 + 5)

(mc/update db :users {:name "Alice"} {$inc {:age -1}})
(mc/find-one-as-map db :users {:name "Alice"})
;; → :age 29 (30 - 1)

;; ── $push ────────────────────────────────────────────────────────────────────
;; 배열 필드에 요소 추가
;; 배열이 없으면 새로 만들어서 추가

(mc/update db :users {:name "Bob"} {$push {:tags "python"}})
(mc/find-one-as-map db :users {:name "Bob"})
;; → :tags ["java" "python"]

;; 배열이 없는 필드에도 동작
(mc/update db :users {:name "Alice"} {$push {:notes "first note"}})
(mc/find-one-as-map db :users {:name "Alice"})
;; → :notes ["first note"]

;; ── $addToSet ────────────────────────────────────────────────────────────────
;; $push 와 비슷하지만 중복을 허용하지 않음
;; 이미 있는 값이면 아무것도 안 함

(mc/update db :users {:name "Bob"} {$addToSet {:tags "python"}})
(mc/find-one-as-map db :users {:name "Bob"})
;; → :tags ["java" "python"] (중복 추가 안 됨)

(mc/update db :users {:name "Bob"} {$addToSet {:tags "clojure"}})
(mc/find-one-as-map db :users {:name "Bob"})
;; → :tags ["java" "python" "clojure"]

;; ── $pull ────────────────────────────────────────────────────────────────────
;; 배열에서 조건에 맞는 요소 제거

(mc/update db :users {:name "Charlie"} {$pull {:tags "haskell"}})
(mc/find-one-as-map db :users {:name "Charlie"})
;; → :tags ["clojure"]

;; ── 여러 연산자 동시에 ────────────────────────────────────────────────────────
;; 한 번의 update 로 여러 필드를 동시에 수정할 수 있습니다

(mc/update db :users {:name "Alice"}
           {$set  {:city "Seoul"}
            $inc  {:score 2}
            $push {:tags "datomic"}})

(mc/find-one-as-map db :users {:name "Alice"})
;; → city "Seoul", score 95, tags [..., "datomic"]
