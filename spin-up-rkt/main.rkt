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
  (display "server started.")
  (set! app (thread (lambda () (run)))))

(define (stop)
  (display "server stopped.")
  (set! app null))

(define (restart)
  (stop)
  (go))
