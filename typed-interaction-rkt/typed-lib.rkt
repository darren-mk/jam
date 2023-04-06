#lang typed/racket

(provide pass add-one)

(: pass (-> Integer Integer))
(define (pass x) x)

(: add-one (-> Integer Integer))
(define (add-one x)
  (add1 x))
