#lang racket

(require "typed-lib.rkt")

(define (see x)
  (if (< x 3)
      (pass 123)
      (pass "abc")))
;; compiles

(time
 (map add-one (range 10000)))
