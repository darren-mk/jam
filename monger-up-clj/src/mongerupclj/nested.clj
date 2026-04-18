(ns mongerupclj.nested
  (:require [monger.collection :as mc]
            [monger.operators :refer [$set $push $pull $in $gt $lte $elemMatch $all]]
            [mongerupclj.db :refer [db]]))

;; ── 데이터 준비 ───────────────────────────────────────────────────────────────
;; 관계형 DB 였다면 별도 테이블로 분리했을 데이터를
;; MongoDB 에서는 문서 안에 내장(embed) 할 수 있습니다

(mc/remove db :people {})

(mc/insert-batch db :people
                 [{:name "Alice"
                   :address {:city "Seoul" :zip "04524" :district "Mapo"}
                   :scores  [88 92 75]
                   :courses [{:title "Clojure" :grade "A"}
                             {:title "MongoDB" :grade "B"}]}
                  {:name "Bob"
                   :address {:city "Busan" :zip "48058" :district "Haeundae"}
                   :scores  [60 70 65]
                   :courses [{:title "Java"    :grade "B"}
                             {:title "Python"  :grade "A"}]}
                  {:name "Charlie"
                   :address {:city "Seoul" :zip "06236" :district "Gangnam"}
                   :scores  [95 98 91]
                   :courses [{:title "Clojure" :grade "A"}
                             {:title "Haskell" :grade "A"}]}])

;; ── 내장 문서 쿼리 ────────────────────────────────────────────────────────────
;; 점 표기법(dot notation) 으로 중첩 필드에 접근합니다
;; Clojure 에서는 문자열 키로 씁니다: "address.city"

;; address.city 가 "Seoul" 인 사람
(mc/find-maps db :people {"address.city" "Seoul"})
;; → Alice, Charlie

;; address.district 가 "Gangnam" 인 사람
(mc/find-maps db :people {"address.district" "Gangnam"})
;; → Charlie

;; 내장 문서 필드 수정
(mc/update db :people {:name "Alice"} {$set {"address.district" "Yongsan"}})
(mc/find-one-as-map db :people {:name "Alice"})
;; → address.district 가 "Yongsan" 으로 변경

;; ── 배열 쿼리 ────────────────────────────────────────────────────────────────
;; 배열 필드에 특정 값이 포함되어 있는지 확인
;; MongoDB 는 배열 안을 자동으로 탐색합니다

;; scores 배열에 95 가 있는 사람
(mc/find-maps db :people {:scores 95})
;; → Charlie

;; scores 배열에 88 또는 60 이 있는 사람
(mc/find-maps db :people {:scores {$in [88 60]}})
;; → Alice (88), Bob (60)

;; scores 배열 중 하나라도 90 초과인 사람
(mc/find-maps db :people {:scores {$gt 90}})
;; → Alice (92), Charlie (95, 98, 91)

;; ── $all ─────────────────────────────────────────────────────────────────────
;; 배열이 지정한 값을 모두 포함하는 문서

;; scores 에 88 과 92 가 둘 다 있는 사람
(mc/find-maps db :people {:scores {$all [88 92]}})
;; → Alice

;; ── $elemMatch ───────────────────────────────────────────────────────────────
;; 배열 안의 한 요소가 여러 조건을 동시에 만족해야 할 때
;;
;; 주의: $elemMatch 없이 쓰면 조건들이 각각 다른 요소에서 만족해도 됨
;;
;; 예: scores 에서 한 점수가 60 이상 70 이하인 사람
(mc/find-maps db :people {:scores {$elemMatch {$gt 60 $lte 70}}})
;; → Bob (70)

;; 비교: $elemMatch 없이 쓰면?
(mc/find-maps db :people {:scores {$gt 60 $lte 70}})
;; → Bob 만 해당 (이 케이스는 우연히 같지만 의미가 다름)
;; 실제로 차이가 나는 예: Alice 의 scores [88 92 75]
;; → $gt 60 은 88 에서 만족, $lte 70 은 없음 → elemMatch 면 제외
;;    하지만 {$gt 60 $lte 70} 이면 같은 요소여야 한다는 보장이 없어
;;    배열 전체에서 각각 탐색하므로 결과가 달라질 수 있음

;; ── 내장 문서 배열 쿼리 ───────────────────────────────────────────────────────
;; courses 는 [{:title ... :grade ...}] 형태의 배열

;; courses 중 title 이 "Clojure" 인 사람
(mc/find-maps db :people {"courses.title" "Clojure"})
;; → Alice, Charlie

;; courses 중 grade 가 "A" 인 과목이 있는 사람
(mc/find-maps db :people {"courses.grade" "A"})
;; → Alice, Bob, Charlie

;; title 이 "Clojure" 이고 grade 가 "A" 인 과목을 가진 사람
;; → $elemMatch 필요! (한 요소에서 두 조건 동시 만족)
(mc/find-maps db :people {:courses {$elemMatch {:title "Clojure" :grade "A"}}})
;; → Alice, Charlie

;; 비교: $elemMatch 없이 쓰면?
(mc/find-maps db :people {"courses.title" "Clojure" "courses.grade" "A"})
;; → 이 경우 title "Clojure" 인 요소와 grade "A" 인 요소가
;;    서로 다른 요소여도 조건을 만족하므로 잘못된 결과가 나올 수 있음
