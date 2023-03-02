(ql:quickload '(:hunchentoot :babel :jonathan :jose :arrow-macros :serapeum :easy-routes))

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

(easy-routes:defroute foo ("/foo/:printable" :method :get
                                  :decorators (@json @auth))
    (&get)
  (print printable)
  (jsonify (alistify darren)))

(defun @json (next)
  (setf (hunchentoot:content-type*) "application/json")
  (funcall next))

(defun @auth (next)
  (let* ((headers (hunchentoot:headers-in*))
         (token (cdr (assoc :authorization headers)))
         (authorized? (authenticate token)))
    (if authorized?
        (funcall next)
        (jsonify unauthorized-msg))))

(defvar *server*
  (hunchentoot:start (make-instance 'easy-routes:routes-acceptor :port 6789)))

(defun start ()
    (print "server started.")
    (hunchentoot:start *server*))

(defun stop ()
    (print "server stopped.")
    (hunchentoot:stop *server*))

(defun re-start ()
  (print "server resetting.")
  (hunchentoot:stop *server*)
  (hunchentoot:start *server*))
