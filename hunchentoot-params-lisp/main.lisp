(ql:quickload '(:hunchentoot :babel :jonathan))

(hunchentoot:define-easy-handler (analyze :uri "/api/analyze") (name)
  ;; get params
  (print name)
  ;; get headers
  (print (hunchentoot:headers-in*))
  ;; get body in given format, byte array
  (print (hunchentoot:raw-post-data))
  ;; get body in string
  (print (babel:octets-to-string (hunchentoot:raw-post-data)))
  ;; get body parsed
  (print (jonathan:parse (babel:octets-to-string (hunchentoot:raw-post-data))))
  ;; return a json
  (setf (hunchentoot:content-type*) "application/json")
  (print (hunchentoot:content-type*))
  (jonathan:to-json '((:name . "Common Lisp")
                      (:born . 1984)
                      (:impls SBCL KCL))
                    :from :alist))

(defvar *server* (make-instance 'hunchentoot:easy-acceptor :port 6789))

(hunchentoot:start *server*)
