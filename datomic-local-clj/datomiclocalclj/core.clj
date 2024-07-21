(ns datomiclocalclj.core
  (:require [datomic.client.api :as d]))

(def cfg
  {:server-type :datomic-local
   :storage-dir :mem
   :system "datomic-samples"})

(def client
  (d/client cfg))

(comment
  (d/create-database
   client {:db-name "movies"}))

(def conn
  (d/connect
   client
   {:db-name "movies"}))

(def movie-schema
  [{:db/ident :movie/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :movie/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The title of the movie"}
   {:db/ident :movie/genre
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The genre of the movie"}
   {:db/ident :movie/release-year
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "The year the movie was released in theaters"}])

(d/transact conn {:tx-data movie-schema})

(d/transact conn {:tx-data
                  [{:movie/id #uuid "be9b8fcb-a402-4e3e-8ae7-e94e1aa31028"
                    :movie/title "Commando"
                    :movie/genre "thriller/action"
                    :movie/release-year 1985}]})

(d/q
 '[:find ?genre
   :where
   [_ :movie/id ?id]
   [_ :movie/genre ?genre]
   [_ :movie/title "Commando"]]
 (d/db conn))
:=> [["thriller/action"]]
