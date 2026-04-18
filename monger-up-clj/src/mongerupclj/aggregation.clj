(ns mongerupclj.aggregation
  (:require [monger.collection :as mc]
            [monger.operators :refer [$gt $gte $lt $lte $sum $avg $min $max
                                      $group $match $sort $project $limit
                                      $skip $unwind $lookup $count]]
            [mongerupclj.db :refer [db]]))

;; ── 집계 파이프라인이란? ──────────────────────────────────────────────────────
;; 문서들이 여러 단계(stage)를 순서대로 통과하면서 변환됩니다
;; Unix 파이프와 동일한 개념입니다
;; 컬렉션 → [$match] → [$group] → [$sort] → 결과
;; mc/aggregate 로 실행하며, 각 stage 는 벡터로 전달합니다

;; ── 데이터 준비 ───────────────────────────────────────────────────────────────

(mc/remove db :orders {})

(mc/insert-batch db :orders
                 [{:customer "Alice" :category "electronics" :amount 350 :qty 2}
                  {:customer "Alice" :category "food"        :amount 80  :qty 5}
                  {:customer "Bob"   :category "electronics" :amount 120 :qty 1}
                  {:customer "Bob"   :category "clothing"    :amount 200 :qty 3}
                  {:customer "Bob"   :category "food"        :amount 45  :qty 2}
                  {:customer "Charlie" :category "electronics" :amount 900 :qty 1}
                  {:customer "Charlie" :category "clothing"    :amount 150 :qty 2}
                  {:customer "Dana"  :category "food"        :amount 60  :qty 4}
                  {:customer "Dana"  :category "food"        :amount 90  :qty 3}])

;; ── $match ───────────────────────────────────────────────────────────────────
;; find-maps 와 동일한 필터링, 파이프라인 앞에 두면 처리량을 줄일 수 있음

(comment
  (mc/aggregate db "orders"
                [{$match {:category "electronics"}}])
  '({:_id #object[org.bson.types.ObjectId 0x2924ef7c "69e39a48b88db05175edeff3"],
     :customer "Alice", :category "electronics", :amount 350, :qty 2}
    {:_id #object[org.bson.types.ObjectId 0x27b46c5c "69e39a48b88db05175edeff5"],
     :customer "Bob", :category "electronics", :amount 120, :qty 1}
    {:_id #object[org.bson.types.ObjectId 0x39d51c76 "69e39a48b88db05175edeff8"],
     :customer "Charlie", :category "electronics", :amount 900, :qty 1}))

;; ── $group ───────────────────────────────────────────────────────────────────
;; 필드 기준으로 문서를 그룹핑하고 집계 연산을 수행
;; :_id → 그룹 기준 필드 ("$필드명" 형식)
;;        nil 이면 전체를 하나로 그룹핑

;; customer 별 총 주문 금액
(comment
  (mc/aggregate db "orders" [{$group {:_id "$customer" :total {$sum "$amount"}}}])
  '({:_id "Alice", :total 430}
    {:_id "Bob", :total 365}
    {:_id "Dana", :total 150}
    {:_id "Charlie", :total 1050}))

;; category 별 평균 금액, 최솟값, 최댓값
(comment
  (mc/aggregate db "orders"
                [{$group {:_id "$category" :avg {$avg "$amount"}
                          :min {$min "$amount"} :max {$max "$amount"}
                          :count {$sum 1}}}])
  '({:_id "clothing", :avg 175.0, :min 150, :max 200, :count 2}
    {:_id "electronics", :avg 456.6666666666667, :min 120, :max 900, :count 3}
    {:_id "food", :avg 68.75, :min 45, :max 90, :count 4}))

;; $sum 1 → 문서 개수 카운트

;; ── $match + $group 조합 ─────────────────────────────────────────────────────
;; 파이프라인의 힘: stage 를 조합해서 복잡한 분석을 표현

;; amount 가 100 이상인 주문만 골라서 customer 별 합산
(mc/aggregate db "orders"
              [{$match {:amount {$gte 100}}}
               {$group {:_id   "$customer"
                        :total {$sum "$amount"}}}])

;; ── $sort ────────────────────────────────────────────────────────────────────
;; 1 → 오름차순, -1 → 내림차순

;; customer 별 총액을 내림차순 정렬
(mc/aggregate db "orders"
              [{$group {:_id   "$customer"
                        :total {$sum "$amount"}}}
               {$sort  {:total -1}}])

;; ── $limit / $skip ───────────────────────────────────────────────────────────
;; 페이지네이션에 활용

;; 총액 상위 2명
(mc/aggregate db "orders"
              [{$group {:_id   "$customer"
                        :total {$sum "$amount"}}}
               {$sort  {:total -1}}
               {$limit 2}])

;; ── $project ─────────────────────────────────────────────────────────────────
;; 출력 필드를 선택하거나 새 필드를 계산
;; 1 → 포함, 0 → 제외

;; amount 와 계산된 필드만 출력
(mc/aggregate db "orders"
              [{$project {:customer 1
                          :amount   1
                          :tax      {$multiply ["$amount" 0.1]}
                          :_id      0}}])
;; → customer, amount, tax(=amount*0.1) 만 출력

;; ── $unwind ──────────────────────────────────────────────────────────────────
;; 배열 필드를 풀어서 각 요소를 별도 문서로 만듦

(mc/remove db :carts {})
(mc/insert-batch db :carts
                 [{:customer "Alice" :items [{:name "keyboard" :price 80}
                                             {:name "mouse"    :price 30}]}
                  {:customer "Bob"   :items [{:name "monitor"  :price 300}]}])

;; items 배열을 unwind 하면 각 item 이 별도 문서가 됨
(mc/aggregate db "carts"
              [{$unwind "$items"}])
;; Alice 문서 1개 → 2개, Bob 문서 1개 → 1개

;; unwind 후 집계: 전체 아이템 중 가장 비싼 것
(mc/aggregate db "carts"
              [{$unwind "$items"}
               {$sort   {"items.price" -1}}
               {$limit  1}
               {$project {:customer 1 :item "$items.name" :price "$items.price" :_id 0}}])

;; ── $lookup ──────────────────────────────────────────────────────────────────
;; 다른 컬렉션과 JOIN (left outer join)
;;
;; :from       → 조인할 컬렉션
;; :localField → 현재 컬렉션의 필드
;; :foreignField → 상대 컬렉션의 필드
;; :as         → 결과를 담을 배열 필드명

(mc/remove db :customers {})
(mc/insert-batch db :customers
                 [{:name "Alice"   :tier "gold"}
                  {:name "Bob"     :tier "silver"}
                  {:name "Charlie" :tier "gold"}
                  {:name "Dana"    :tier "bronze"}])

;; orders 에 customers 정보를 join
(comment
  (mc/aggregate db "orders"
                [{$lookup {:from "customers"
                           :localField "customer"
                           :foreignField "name" :as "customer_info"}}
                 {$project {:customer 1 :amount 1 :customer_info 1 :_id 0}}])
  '({:customer "Alice", :amount 350, :customer_info []}
    {:customer "Alice", :amount 80, :customer_info []}
    {:customer "Bob", :amount 120, :customer_info []}
    {:customer "Bob", :amount 200, :customer_info []}
    {:customer "Bob", :amount 45, :customer_info []}
    {:customer "Charlie", :amount 900, :customer_info []}
    {:customer "Charlie", :amount 150, :customer_info []}
    {:customer "Dana", :amount 60, :customer_info []}
    {:customer "Dana", :amount 90, :customer_info []}))

;; → 각 주문에 customer_info 배열이 추가됨

;; ── 종합 예제 ─────────────────────────────────────────────────────────────────
;; gold 등급 고객의 category 별 총 주문금액, 내림차순 정렬

(mc/aggregate db "orders"
              [{$lookup {:from "customers"
                         :localField "customer"
                         :foreignField "name"
                         :as "customer_info"}}
               {$unwind "$customer_info"}
               {$match {"customer_info.tier" "gold"}}
               {$group {:_id "$category"
                        :total {$sum "$amount"}}}
               {$sort {:total -1}}])
