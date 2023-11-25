(ql:quickload :rutils)

(in-package :rtl-user)

(named-readtables:in-readtable rutils-readtable)

(defvar f1
  #`(+ % %%))
;; #<FUNCTION (LAMBDA (&OPTIONAL % %%)) {7005B473CB}>
(call f1 2 3) ;; 5
(-> 3 (+ 5)) ;; 8

(defparameter a
  #h(:a 1 :b 2))
;; #<HASH-TABLE :TEST EQL :COUNT 2 {7008547EE3}>

(defparameter b
  #v(1 2 3))
;; #(1 2 3)
