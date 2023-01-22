#lang racket/base

(require csv-reading)
(require racket/string)

(define make-food-csv-reader
  (make-csv-reader-maker
   '((strip-leading-whitespace?  . #t)
     (strip-trailing-whitespace? . #t))))

(define next-row
  (make-food-csv-reader (open-input-file "input.csv")))

(list? (next-row)) ;; #t


(csv->sxml (open-input-file "input.csv")
           'friend
           '(name quantity weight))

(map (lambda (x) (string-trim x))
 (car
  (csv->list (open-input-file "input.csv"))))

