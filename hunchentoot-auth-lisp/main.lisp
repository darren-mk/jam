(ql:quickload '(:hunchentoot :babel :jonathan :jose :arrow-macros :yason))

(defvar *key* (ironclad:ascii-string-to-byte-array "my$ecret"))

(defvar *token* (jose:encode :hs256 *key* '(("question" . "answer"))))
;; CL-USER> *token*
;; "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJxdWVzdGlvbiI6ImFuc3dlciJ9.fkC4bywF9TWUWO-mMyQ1CoNbPc7ZxbAC_T47l-W8cX8"

;; (jose:decode :hs256 *key* *token)
;; CL-USER> (jose:decode :hs256 *key* *token*)
;; (("question" . "answer"))
;; (("alg" . "HS256") ("typ" . "JWT"))

(defun authenticate (token)
  (print token)
  (handler-case (jose:decode :hs256 *key* token)
    (error () nil)))

(defun get-token (headers)
  (print headers)
  (let ((f (lambda (pair) (eq :authorization (car pair)))))
    (arrow-macros:-> (remove-if-not f headers) car cdr)))

(hunchentoot:define-easy-handler (analyze :uri "/api/analyze") ()
  (let* ((headers (hunchentoot:headers-in*))
         (token (cdr (assoc :authorization headers)))
         (authenticated? (authenticate token)))
    (setf (hunchentoot:content-type*) "application/json")
    (if authenticated?
        (jonathan:to-json '((:result . "authorized")) :from :alist)
        (jonathan:to-json '((:result . "unauthorized")) :from :alist))))

(defvar *server* (make-instance 'hunchentoot:easy-acceptor :port 6789))

(hunchentoot:start *server*)
