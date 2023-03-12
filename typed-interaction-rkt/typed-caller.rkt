#lang typed/racket

(require/typed "untyped-lib.rkt"
  [pass (-> Integer Integer)])

(time (pass 123))
