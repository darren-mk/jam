(ns psqlperformclj.core
  (:require
   [clojure.string :as str]
   [next.jdbc :as jdbc]))

(def db {:dbtype "postgres" :dbname "helenium"})

(def ds (jdbc/get-datasource db))

;; table schema

(jdbc/execute!
 ds [(str "create table address ("
          "id serial primary key, "
          "name text, "
          "email text)")])

(time
 (jdbc/execute!
  ds [(str "insert into address(name, email) values "
           (str/join ", " (mapv (fn [i] (str "('name-" i "', 'email-" i "')"))
                                (range 10000))))]))
;; "Elapsed time: 92.019375 msecs"

(time
 (jdbc/execute! ds ["select * from address where name = 'name-8888';"]))
;; "Elapsed time: 22.84925 msecs"
;; "Elapsed time: 21.541875 msecs"
;; "Elapsed time: 18.861334 msecs"
;; "Elapsed time: 15.300458 msecs"
;; "Elapsed time: 26.232042 msecs"

;; jsonb

(jdbc/execute!
 ds [(str "create table food ("
          "id serial primary key, "
          "blob jsonb)")])

(time
 (jdbc/execute!
  ds [(str "insert into food (blob) values "
           (str/join ", " (mapv (fn [i] (str "('name-" i "', 'email-" i "')"))
                                (range 10000))))]))

