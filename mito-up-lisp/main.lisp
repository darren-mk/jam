(ql:quickload '(:mito))

;; create database in postgres
;; postgres=# create database mitouplisp;

;; connect to the db
(mito:connect-toplevel :postgres :database-name "mitouplisp")

(mito:deftable user ()
  ((fname :col-type (or (:text) :null))
   (lname :col-type (or (:text) :null))
   (email :col-type (or (:varchar 128) :null))))

(mito:ensure-table-exists 'user)

(mito:migration-expressions 'user)

(mito:migrate-table 'user)

(defvar me (make-instance 'user :name "darren" :email "fake-email@email.com"))

(mito:insert-dao me)

(mito:count-dao 'user)

(mito:select-dao 'user)
