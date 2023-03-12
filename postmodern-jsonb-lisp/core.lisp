(ql:quickload :postmodern)

(postmodern:connect-toplevel "helenium" "darren" "..." "localhost")

(defclass blob ()
  ((id :col-type uuid :initarg :id :accessor id)
   (data :col-type jsonb :initarg :data :accessor data))
  (:metaclass postmodern:dao-class)
  (:keys id))

(postmodern:execute (postmodern:dao-table-definition 'blob))

(defparameter luck-a
  (make-instance 'blob
                 :id "40e6215d-b5c6-4896-987c-f30f3678f608"
                 :data "{\"age\": 12345}"))

(postmodern:insert-dao luck-a)

(defparameter returned-luck-a
  (postmodern:get-dao 'blob "40e6215d-b5c6-4896-987c-f30f3678f608"))

(type-of returned-luck-a)
;; blob

(postmodern:query (:select 'id 'data :from 'blob))
;; => (("40e6215d-b5c6-4896-987c-f30f3678f608" "{\"age\": 12345}")), 1
