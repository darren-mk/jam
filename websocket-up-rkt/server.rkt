#lang racket

(require net/rfc6455)

(define (serve-ws)
  (ws-serve #:port 8081
            (lambda (c s)
              (displayln "got a request!")
              (displayln (s))
              (ws-send! c "hello man"))))
