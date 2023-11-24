#lang racket

(require db)
(require sql)

(define pgc
  (postgresql-connect #:user "darren"
                      #:database "unadb"
                      #:password ""))

(query-rows pgc (select id #:from writer))

(query-value pgc "select date '25-dec-1980'")
