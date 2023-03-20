(ns datalevinupclj.core
  (:require
   [datalevin.core :as d]))

(def schema {:aka  {:db/cardinality :db.cardinality/many}
             :name {:db/valueType :db.type/string
                    :db/unique :db.unique/identity}})

(def conn (d/get-conn "/tmp/datalevin/mydb" schema))

(def data (mapv
           (fn [i] {:db/id i :name (str "name-" i)
                    :aka [(str "aka-" i) (str "aka-" i "-alt")]})
           (range 10000)))

(time (d/transact! conn data))
;; "Elapsed time: 287.809791 msecs"

(time
 (d/q '[:find [?name ?aka]
        :in $ ?aka
        :where
        [?e :aka ?aka]
        [?e :name ?name]]
      (d/db conn)
      "aka-8888"))
;; "Elapsed time: 32.324 msecs"
;; "Elapsed time: 5.356834 msecs"
;; "Elapsed time: 5.241292 msecs"
;; "Elapsed time: 8.315417 msecs"

(def data-wo-schema
  (mapv
   (fn [i] {:db/id i :nickname (str "nickname-" i)
            :funny [(str "funny-" i) (str "funny-" i "-alt")]})
   (range 20000 30000)))

(time (d/transact! conn data-wo-schema))
;; "Elapsed time: 276.655208 msecs"

(time
 (d/q '[:find [?nickname ?funny]
        :in $ ?funny
        :where
        [?e :funny ?funny]
        [?e :nickname ?nickname]]
      (d/db conn)
      "funny-28888"))
;; "Elapsed time: 22.896708 msecs"
;; "Elapsed time: 2.132583 msecs"
;; "Elapsed time: 2.65975 msecs"

(time
 (d/q '[:find (pull ?e [*])
        :in $ ?aka
        :where
        [?e :aka ?aka]]
      (d/db conn)
      "aka-8888"))
;; "Elapsed time: 5.644041 msecs"
;; "Elapsed time: 5.705458 msecs"
;; "Elapsed time: 5.896334 msecs"

(time
 (d/q '[:find (pull ?e [*])
        :in $ ?funny
        :where
        [?e :funny ?funny]]
      (d/db conn)
      "funny-28888"))
;; "Elapsed time: 3.523125 msecs"
;; "Elapsed time: 3.608042 msecs"
;; "Elapsed time: 3.438625 msecs"
