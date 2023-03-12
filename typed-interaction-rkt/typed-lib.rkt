#lang typed/racket

(provide pass)

(: pass (-> Integer Integer))
(define (pass x) x)
