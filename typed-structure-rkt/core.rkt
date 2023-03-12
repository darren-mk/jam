#lang typed/racket

(struct person
  ((name : String)
   (age : Integer)
   (height : Float))
  #:transparent)

(define me (person "Darren" 80 171.2))

me
;; - : person
;; (person "Darren" 80 171.2)

(person-name me)
;; - : String
;; "Darren"
