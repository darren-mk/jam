(ns elasticsearch-basics.aggregations
  (:require [clj-http.client :as http]
            [elasticsearch-basics.helper :refer [url as-json index]]))

(defn aggregate
  "`size` is zero as source docs are not expected."
  [aggs]
  (let [res (:body (http/get (url "/" index "/_search")
                             (as-json {:size 0 :aggs aggs})))]
    (:aggregations res)))

;; 1. Metric Aggregations — 숫자 계산
(comment
  ;; 평균 출판연도
  (aggregate {:avg_year {:avg {:field :year}}})
  :=> {:avg_year {:value 2002.8}}

  ;; 최솟값 / 최댓값
  (aggregate {:min_year {:min {:field :year}}
              :max_year {:max {:field :year}}})
  :=> {:min_year {:value 1984.0}
       :max_year {:value 2017.0}}

  ;; 전체 문서 수
  (aggregate {:total {:value_count {:field :year}}})
  :=> {:total {:value 5}})

;; 2. Bucket Aggregations — 그룹핑
(comment
  ;; 저자별 책 수 (terms: 값이 같은 것끼리 묶기)
  (aggregate {:by_author {:terms {:field :author}}})
  :=> {:by_author
       {:doc_count_error_upper_bound 0,
        :sum_other_doc_count 0,
        :buckets
        [{:key "Robert Martin", :doc_count 2}
         {:key "David Thomas", :doc_count 1}
         {:key "Harold Abelson", :doc_count 1}
         {:key "Steve McConnell", :doc_count 1}]}}

  ;; 출판연도 범위별 묶기
  (aggregate {:by_era {:range {:field :year
                               :ranges [{:key "고전" :to 2000}
                                        {:key "2000년대" :from 2000 :to 2010}
                                        {:key "최근" :from 2010}]}}})
  :=> {:by_era
       {:buckets
        [{:key "고전", :to 2000.0, :doc_count 2}
         {:key "2000년대", :from 2000.0, :to 2010.0, :doc_count 1}
         {:key "최근", :from 2010.0, :doc_count 2}]}})

;; 3. Bucket + Metric 중첩 — 저자별 평균 출판연도
(comment
  (aggregate {:by_author {:terms {:field :author}
                          :aggs {:avg_year {:avg {:field :year}}}}})
  :=> {:by_author
       {:doc_count_error_upper_bound 0,
        :sum_other_doc_count 0,
        :buckets
        [{:key "Robert Martin", :doc_count 2, :avg_year {:value 2013.5}}
         {:key "David Thomas", :doc_count 1, :avg_year {:value 1999.0}}
         {:key "Harold Abelson", :doc_count 1, :avg_year {:value 1984.0}}
         {:key "Steve McConnell",
          :doc_count 1,
          :avg_year {:value 2004.0}}]}})
