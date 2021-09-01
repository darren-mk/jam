(ns interview.core-test
  (:require [clojure.test :refer :all]
            [interview.core :refer :all]))

(deftest get-floor-index-test
  (testing "Get 5th floor's index in the floors."
    (= 6 (get-floor-index 5))))

(deftest move-test
  (testing "Go up from 1st floor."
    (is (= 2 (move 1 :up))))
  (testing "Go down from 3rd floor."
    (is (= 2 (move 3 :down))))
  (testing "Go up to 5th skipping non-existent 4th."
    (is (= 5 (move 3 :up))))
  (testing "Go down to 3rd skipping non-existtent 4th."
    (is (= 3 (move 5 :down))))
  (testing "Niether go up nor down from 2nd floor."
    (is (= 1 (move 1 :there))))
  (testing "Trying to go below the lowest floor."
    (is (= -2 (move -2 :down))))
  (testing "Trying to go above the highest floor."
    (is (= 5 (move 5 :up)))))

(deftest single-plan-test
  (testing "Want to go to 3rd floor from 1st floor."
    (is (= [:up :up :open :close]
           (single-plan 1 3))))
  (testing "Want to go to 1rd floor from 5th floor."
    (is (= [:down :down :down :open :close]
           (single-plan 5 1)))))

(deftest create-plans-test
  (testing "2 persons to go 3rd and 5th from 1st floor."
    (is (= [:up :up :open :close :up :open :close]
           (create-plans 1 [3 5]))))
 (testing "2 persons to go 3rd and 2nd from 5th floor."
    (is (= [:down :open :close :down :open :close]
           (create-plans 5 [3 2])))))
