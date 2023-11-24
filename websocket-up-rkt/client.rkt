#lang racket

(require net/rfc6455)
(require net/url)

(define c
  (ws-connect
   (string->url "ws://localhost:8081/")))

(define (send-msg msg)
  (ws-send! c msg))
