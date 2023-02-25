#lang racket

(require (planet dmac/spin))

(get "/"
     (lambda () "Hello!"))

(post "/hi" (lambda (req)
             (print req)
             (string-append "Hello, "
                            (params req 'name) "!")))

(get "/hi/:name"
     (lambda (req)
       (string-append "Hello, " (params req 'name) "!")))

(define app null)

(define (go)
  (set! app (thread (lambda () (run)))))
