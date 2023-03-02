(ql:quickload :postmodern)
(use-package :postmodern)

(connect-toplevel "helenium" "darren" "..." "localhost")

(defclass country ()
    ((name :col-type string :initarg :name
            :reader country-name)
    (inhabitants :col-type integer :initarg :inhabitants
                :accessor country-inhabitants)
    (sovereign :col-type (or db-null string) :initarg :sovereign
                :accessor country-sovereign))
    (:metaclass dao-class)
    (:keys name))

(execute (dao-table-definition 'country))

(defparameter france
  (make-instance 'country
                 :name "France"
                 :inhabitants 300
                 :sovereign "Paris"))

(insert-dao france)

(defparameter returned-france
  (get-dao 'country "France"))

(type-of returned-france)
;; country

(query (:select 'name 'sovereign
         :from 'country))
;; (("France" "Paris"))
