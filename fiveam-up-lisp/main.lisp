;; https://lispcookbook.github.io/cl-cookbook/testing.html

(ql:quickload :fiveam)

(defpackage fiveam-up-lisp
  (:use #:cl
        #:fiveam))

(in-package :fiveam-up-lisp)

(setf fiveam:*run-test-when-defined* t)

(fiveam:def-suite my-system
  :description "testing proof of concept")

(fiveam:test closed-value
  (let ((result (+ 1 2)))
    (fiveam:is (= 3 result))))

(defun add-two-nums (a b)
  (+ a b))

(fiveam:test closed-function
  (fiveam:is (= 3 (add-two-nums 1 2))))

(fiveam:test randomtest
  (fiveam:for-all ((a (gen-integer :min 1 :max 3))
                   (b (gen-integer :min 4 :max 6)))
    "Test random tests."
    (fiveam:is (<= a b))))
