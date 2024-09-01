(ns datomiclocalclj.core
  (:require [datomic.client.api :as d]))

(def config
  {:server-type :datomic-local
   :storage-dir :mem
   :system "datomic-samples"})

(def client
  (d/client config))

(d/create-database
 client {:db-name "movies"})

(defn conn []
  (d/connect
   client
   {:db-name "movies"}))

(def movie-schema
  [{:db/ident :director/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :director/last-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :movie/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :movie/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :movie/director-id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}])

(d/transact (conn) {:tx-data movie-schema})

(def nolan
  {:director/id #uuid "e6d652ce-bdc7-47e9-aa74-0ff6143b50ff"
   :director/last-name "Nolan"})

(d/transact (conn) {:tx-data [nolan]})

(def inception
  {:movie/id #uuid "ff10135f-25bd-4cd0-8a77-7202ab2d23ec"
   :movie/title "Inception"
   :movie/director-id (:director/id nolan)})

(def memento
  {:movie/id #uuid "bfba3289-090b-4c11-966f-2c0da7888e3b"
   :movie/title "Memento"
   :movie/director-id (:director/id nolan)})

(d/transact (conn) {:tx-data [inception memento]})

(def db (d/db (conn)))

(d/q {:query '[:find ?id
               :where
               [_ :director/id ?id]
               [_ :director/last-name "Nolan"]]
      :args [db]})
;; => [[#uuid "e6d652ce-bdc7-47e9-aa74-0ff6143b50ff"]]

(d/q {:query '[:find ?d
               :where
               [?d :director/last-name "Nolan"]]
      :args [db]})
;; => [[96757023244366]]

(d/q {:query '[:find (pull ?e [:director/id :director/last-name])
               :where
               [?e :director/id ?id]
               [?e :director/last-name "Nolan"]]
      :args [db]})
;; => [[#:director{:id #uuid "e6d652ce-bdc7-47e9-aa74-0ff6143b50ff",
;;                 :last-name "Nolan"}]]

(d/q {:query '[:find (pull ?e [*])
               :where
               [?e :director/id ?id]
               [?e :director/last-name "Nolan"]]
      :args [db]})
;; => [[{:db/id 96757023244366,
;;       :director/id #uuid "e6d652ce-bdc7-47e9-aa74-0ff6143b50ff",
;;       :director/last-name "Nolan"}]]


(d/q {:query '{:find [(pull ?e [*])]
               :where [[?e :director/id ?id]
                       [?e :director/last-name "Nolan"]]}
      :args [db]})
;; => [[{:db/id 96757023244366,
;;       :director/id #uuid "e6d652ce-bdc7-47e9-aa74-0ff6143b50ff",
;;       :director/last-name "Nolan"}]]

(d/q {:query '{:find [?id]
               :in [$ ?last-name]
               :where [[_ :director/id ?id]
                       [_ :director/last-name ?last-name]]}
      :args [db "Nolan"]})

(d/q {:query '{:find [?last-name ?movie-id ?movie-title]
               :in [$ ?last-name]
               :where [[?director :director/id ?id]
                       [?director :director/last-name ?last-name]
                       [?movie :movie/id ?movie-id]
                       [?movie :movie/title ?movie-title]
                       [?movie :movie/director-id ?id]]}
      :args [db "Nolan"]})
