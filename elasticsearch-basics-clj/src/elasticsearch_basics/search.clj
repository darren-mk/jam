(ns elasticsearch-basics.search
  (:require [clj-http.client :as http]
            [elasticsearch-basics.helper :refer [url as-json index]]))

(defn search [query]
  (let [res (:body (http/get (url "/" index "/_search")
                             (as-json {:query query})))]
    (get-in res [:hits :hits])))

(defn search-all []
  (search {:match_all {}}))

(comment
  (search-all)
  :=> [{:_index "books",
        :_id "2",
        :_score 1.0,
        :_source {:title "The Pragmatic Programmer",
                  :author "David Thomas",
                  :year 1999,
                  :content "실용적인 프로그래머"}}
       {:_index "books",
        :_id "3",
        :_score 1.0,
        :_source {:title "SICP",
                  :author "Harold Abelson",
                  :year 1984,
                  :content "컴퓨터 프로그램의 구조와 해석"}}
       {:_index "books",
        :_id "1",
        :_score 1.0,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}])

(defn search-by-title [title]
  (search {:match {:title title}}))

(comment
  (search-by-title "code")
  :=> [{:_index "books",
        :_id "1",
        :_score 0.9808291,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}])

(defn search-by-author [author]
  (search {:term {:author author}}))

(comment
  (search-by-author "Robert Martin")
  :=> [{:_index "books"
        :_id "1"
        :_score 0.9808291
        :_source {:title "Clean Code"
                  :author "Robert Martin"
                  :year 2010
                  :content "소프트웨어 장인 정신"}}])

;; 5. Bool 쿼리
;; must — 반드시 일치 (스코어에 영향)
;; filter — 반드시 일치 (스코어에 영향 없음, 캐싱되어 빠름)
;; should — 하나 이상 일치하면 됨 (OR)
;; must_not — 일치하면 제외

(defn bool-search [bool-clause]
  (search {:bool bool-clause}))

(comment
  ;; must: 제목에 "code" 포함 AND 저자가 "Robert Martin"

  (bool-search {:must [{:match {:title "code"}}
                       {:term {:author "Robert Martin"}}]})
  :=> [{:_index "books",
        :_id "1",
        :_score 1.9616582,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}]

  (bool-search {:should [{:match {:title "code"}}
                         {:term {:author "Robert Martin"}}]})
  :=> [{:_index "books",
        :_id "1",
        :_score 1.7509375,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 0.87546873,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}
       {:_index "books",
        :_id "5",
        :_score 0.87546873,
        :_source {:title "Clean Architecture",
                  :author "Robert Martin",
                  :year 2017,
                  :content "소프트웨어 아키텍처의 원칙"}}]

  ;; filter: 2000년 이후 출판 (스코어 불필요하므로 filter 사용)
  (bool-search {:filter [{:range {:year {:gte 2000}}}]})
  :=> [{:_index "books",
        :_id "1",
        :_score 0.0,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 0.0,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}
       {:_index "books",
        :_id "5",
        :_score 0.0,
        :_source {:title "Clean Architecture",
                  :author "Robert Martin",
                  :year 2017,
                  :content "소프트웨어 아키텍처의 원칙"}}]

  (bool-search {:must [{:range {:year {:gte 2000}}}]})
  :=> [{:_index "books",
        :_id "1",
        :_score 1.0,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 1.0,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}
       {:_index "books",
        :_id "5",
        :_score 1.0,
        :_source {:title "Clean Architecture",
                  :author "Robert Martin",
                  :year 2017,
                  :content "소프트웨어 아키텍처의 원칙"}}]

  ;; must + filter: 제목에 "code" 포함 AND 2000년 이후
  (bool-search {:must [{:match {:title "code"}}]
                :filter [{:range {:year {:gte 2000}}}]})
  :=> [{:_index "books",
        :_id "1",
        :_score 0.87546873,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 0.87546873,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}]

  (bool-search {:must [{:match {:title "code"}}
                       {:range {:year {:gte 2000}}}]})
  :=> [{:_index "books",
        :_id "1",
        :_score 1.8754687,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 1.8754687,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}]

  ;; should: 제목에 "code" 또는 저자가 "Harold Abelson"
  (bool-search {:should [{:match {:title "code"}}
                         {:term {:author "Harold Abelson"}}]})
  :=> [{:_index "books",
        :_id "3",
        :_score 1.3862942,
        :_source {:title "SICP",
                  :author "Harold Abelson",
                  :year 1984,
                  :content "컴퓨터 프로그램의 구조와 해석"}}
       {:_index "books",
        :_id "1",
        :_score 0.87546873,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 0.87546873,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}]

  ;; must_not: 저자가 "David Thomas"가 아닌 책

  (bool-search {:must_not [{:term {:author "David Thomas"}}]})
  :=> [{:_index "books",
        :_id "3",
        :_score 0.0,
        :_source {:title "SICP",
                  :author "Harold Abelson",
                  :year 1984,
                  :content "컴퓨터 프로그램의 구조와 해석"}}
       {:_index "books",
        :_id "1",
        :_score 0.0,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 0.0,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}
       {:_index "books",
        :_id "5",
        :_score 0.0,
        :_source {:title "Clean Architecture",
                  :author "Robert Martin",
                  :year 2017,
                  :content "소프트웨어 아키텍처의 원칙"}}])

;; 6. 정렬 (Sorting)
;; 기본은 _score 내림차순. 필드 기준으로 바꿀 수 있음.
;; asc: 오름차순, desc: 내림차순
;; keyword/integer 필드만 정렬 가능 — text 필드는 불가

(defn search-sorted [query sort-field order]
  (let [res (:body (http/get (url "/" index "/_search")
                             (as-json {:query query
                                       :sort [{sort-field {:order order}}]})))]
    (get-in res [:hits :hits])))

(comment
  ;; 출판연도 오름차순
  (first (search-sorted {:match_all {}} :year "asc"))
  :=> {:_index "books",
       :_id "3",
       :_score nil,
       :_source {:title "SICP",
                 :author "Harold Abelson",
                 :year 1984,
                 :content "컴퓨터 프로그램의 구조와 해석"},
       :sort [1984]}

  ;; 출판연도 내림차순
  (count (search-sorted {:match_all {}} :year "desc"))
  :=> 5
  (first (search-sorted {:match_all {}} :year "desc"))
  :=> {:_index "books",
       :_id "5",
       :_score nil,
       :_source {:title "Clean Architecture",
                 :author "Robert Martin",
                 :year 2017,
                 :content "소프트웨어 아키텍처의 원칙"},
       :sort [2017]}

  (= (first (search-sorted {:match_all {}} :year "desc"))
     (first (search-sorted {:match_all {}} :year :desc)))
  :=> true

  ;; 저자 알파벳순 (keyword 필드라 정렬 가능)
  (first (search-sorted {:match_all {}} :author "asc"))
  :=> {:_index "books",
       :_id "2",
       :_score nil,
       :_source {:title "The Pragmatic Programmer",
                 :author "David Thomas",
                 :year 1999,
                 :content "실용적인 프로그래머"},
       :sort ["David Thomas"]})

;; 7. 페이지네이션 (Pagination)
;; from: 건너뛸 문서 수 (offset), size: 반환할 문서 수
;; 기본값: from=0, size=10

(defn search-paged [query from size]
  (let [res (:body (http/get (url "/" index "/_search")
                             (as-json {:query query
                                       :from from
                                       :size size})))]
    (get-in res [:hits :hits])))

(comment
  ;; 첫 번째 페이지 — 2개씩
  (search-paged {:match_all {}} 0 2)
  :=> [{:_index "books",
        :_id "2",
        :_score 1.0,
        :_source {:title "The Pragmatic Programmer",
                  :author "David Thomas",
                  :year 1999,
                  :content "실용적인 프로그래머"}}
       {:_index "books",
        :_id "3",
        :_score 1.0,
        :_source {:title "SICP",
                  :author "Harold Abelson",
                  :year 1984,
                  :content "컴퓨터 프로그램의 구조와 해석"}}]

  ;; 두 번째 페이지
  (search-paged {:match_all {}} 2 2)
  :=> [{:_index "books",
        :_id "1",
        :_score 1.0,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2010,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 1.0,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}]

  ;; 세 번째 페이지
  (search-paged {:match_all {}} 4 2)
  :=> [{:_index "books",
        :_id "5",
        :_score 1.0,
        :_source {:title "Clean Architecture",
                  :author "Robert Martin",
                  :year 2017,
                  :content "소프트웨어 아키텍처의 원칙"}}])

;; 8. Multi-match — 여러 필드를 동시에 검색
;; match는 단일 필드, multi_match는 여러 필드에 같은 키워드를 검색
;; 필드명 뒤에 ^숫자 로 boost(가중치) 부여 가능 — 높을수록 스코어 기여도 높음

(defn search-multi [keyword fields]
  (search {:multi_match {:query keyword :fields fields}}))

(comment
  ;; title과 content 둘 다에서 "소프트웨어" 검색
  (search-multi "Code" [:title :content])
  :=> [{:_index "books",
        :_id "3",
        :_score 1.2598237,
        :_source {:title "SICP",
                  :author "Harold Abelson",
                  :year 1984,
                  :content "code as data, data as code"}}
       {:_index "books",
        :_id "1",
        :_score 1.0296195,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2008,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 1.0296195,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}
       {:_index "books",
        :_id "2",
        :_score 0.87255895,
        :_source {:title "The Pragmatic Programmer",
                  :author "David Thomas",
                  :year 1999,
                  :content "writing good code and practical programming"}}]

;; title에 더 높은 가중치 부여 (title^3 = title 스코어를 3배)
  (search-multi "code" ["title^3" "content"])
  :=> [{:_index "books",
        :_id "1",
        :_score 3.0888584,
        :_source {:title "Clean Code",
                  :author "Robert Martin",
                  :year 2008,
                  :content "소프트웨어 장인 정신"}}
       {:_index "books",
        :_id "4",
        :_score 3.0888584,
        :_source {:title "Code Complete",
                  :author "Steve McConnell",
                  :year 2004,
                  :content "소프트웨어 구현의 모든 것"}}
       {:_index "books",
        :_id "3",
        :_score 1.2598237,
        :_source {:title "SICP",
                  :author "Harold Abelson",
                  :year 1984,
                  :content "code as data, data as code"}}
       {:_index "books",
        :_id "2",
        :_score 0.87255895,
        :_source {:title "The Pragmatic Programmer",
                  :author "David Thomas",
                  :year 1999,
                  :content "writing good code and practical programming"}}])
