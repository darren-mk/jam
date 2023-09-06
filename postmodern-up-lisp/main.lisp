(ql:quickload :postmodern)
(in-package :postmodern)

(connect-toplevel "helenium" "darren" "..." "localhost")

(defclass country ()
  ((name :col-type string
         :initarg :name
         :reader country-name)
   (inhabitants :col-type integer
                :initarg :inhabitants
                :accessor country-inhabitants)
   (sovereign :col-type (or db-null string)
              :initarg :sovereign
              :accessor country-sovereign))
  (:metaclass dao-class)
  (:keys name))

(execute (dao-table-definition 'country))

(defvar korea
  (list 'name "Korea"
        'inhabitants 2000000
        'sovereign "Nobody"))

(query (concatenate
        'list
        (list :insert-into 'country :set)
        korea))


(query '(:insert-into 'country
        :set 'name "The Netherlands"
                     'inhabitants 16800000
                     'sovereign "Willem-Alexander"))

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
