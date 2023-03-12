;; https://parenscript.common-lisp.dev/tutorial.html

(mapc #'ql:quickload
      '(:cl-fad :cl-who :hunchentoot :parenscript))

(defpackage ps-tutorial
  (:use :cl :hunchentoot :cl-who :parenscript :cl-fad))

(in-package ps-tutorial)

(setq cl-who:*attribute-quote-char* #\")

(start (make-instance 'easy-acceptor :port 8080))

(define-easy-handler (example1 :uri "/example1") ()
  (with-html-output-to-string (s)
    (:html
     (:head (:title "Parenscript tutorial: 1st example"))
     (:body (:h2 "Parenscript tutorial: 1st example")
            "Please click the link below." :br
            (:a :href "#" :onclick (ps (alert "Hello World"))
                "Hello World")))))

(define-easy-handler (example2 :uri "/example2") ()
  (with-html-output-to-string (s)
    (:html
     (:head
      (:title "Parenscript tutorial: 2nd example")
      (:script :type "text/javascript"
               (str (ps
                      (defun greeting-callback ()
                        (alert "Hello World"))))))
     (:body
      (:h2 "Parenscript tutorial: 2nd example")
      (:a :href "#" :onclick (ps (greeting-callback))
          "Hello World")))))
