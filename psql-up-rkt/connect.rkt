#lang racket

(require db)

(define pgc
  (postgresql-connect #:user "darren"
                      #:database "unadb"
                      #:password ""))

(time
 (query-value
  pgc
  "select fname from writer where email = 'darren@em.com';"))
;; cpu time: 0 real time: 2 gc time: 0
