(ql:quickload '(:hunchentoot :babel :jonathan :jose :arrow-macros :serapeum))

(setf *print-case* :downcase)

(defparameter *key*
  (ironclad:ascii-string-to-byte-array "my-temp-secret"))

(defparameter *token*
  (jose:encode :hs256 *key* '((:email . "abc@def.com")
                              (:stamp . (get-universal-time)))))

(defparameter decoded
  (jose:decode :hs256 *key* *token*))

(defun authenticate (token)
  (print token)
  (handler-case (jose:decode :hs256 *key* token)
    (error () nil)))

(defun get-token (headers)
  (print headers)
  (let ((f (lambda (pair) (eq :authorization (car pair)))))
    (arrow-macros:-> (remove-if-not f headers) car cdr)))

(defclass user ()
  ((id :type integer :initarg :id :accessor id-of)
   (name :type string :initarg :name :accessor name-of)))

(defparameter darren
  (make-instance 'user :id "abc123" :name "Darren Kim"))

(defmethod alistify ((user user))
  (let ((id (id-of user))
        (name (name-of user)))
   `((:id . ,id) (:name . ,name))))

(defmethod jsonify (alist)
  (jonathan:to-json alist :from :alist))

(defmethod %to-json ((user user))
  (jonathan:with-object
    (jonathan:write-key-value "id" (slot-value user 'id))
    (jonathan:write-key-value "name" (slot-value user 'name))))

;; (jonathan:to-json (make-instance 'user :id 1 :name "Rudolph"))
;; => "{\"id\":1,\"name\":\"Rudolph\"}"

(defvar unauthorized-msg
  '((:result . "unauthorized")))

(hunchentoot:define-easy-handler (analyze :uri "/api/analyze") ()
  (let* ((headers (hunchentoot:headers-in*))
         (token (cdr (assoc :authorization headers)))
         (authenticated? (authenticate token)))
    (setf (hunchentoot:content-type*) "application/json")
    (if authenticated?
        (arrow-macros:-> darren alistify jsonify)
        (jsonify unauthorized-msg))))

(defvar *server* (make-instance 'hunchentoot:easy-acceptor :port 6789))

(defun start ()
    (print "server started.")
    (hunchentoot:start *server*))

(defun stop ()
    (print "server stopped.")
    (hunchentoot:stop *server*))
