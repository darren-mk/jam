(ns xtdbupclj.core
  (:require
   [clojure.java.io :as io]
   [xtdb.api :as xt]))

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(def xtdb-node (start-xtdb!))

(defn stop-xtdb! []
  (.close xtdb-node))

(def data
  (mapv
   (fn [i] [::xt/put
            {:xt/id i
            :user/name (str "name-" i)
            :user/aka [(str "aka-" i) (str "aka-" i "-alt")]}])
   (range 10000)))

(time (xt/submit-tx xtdb-node data))
;; "Elapsed time: 264.786625 msecs"

(time (xt/q (xt/db xtdb-node)
      '{:find [e]
        :where [[e :user/name "name-8888"]]}))
;; "Elapsed time: 3.91725 msecs"
;; "Elapsed time: 1.614917 msecs"
;; "Elapsed time: 1.687791 msecs"

(time (xt/q (xt/db xtdb-node)
      '{:find [e]
        :where [[e :user/aka "aka-8888"]]}))
;; "Elapsed time: 1.505959 msecs"
;; "Elapsed time: 1.622334 msecs"
;; "Elapsed time: 1.629167 msecs"

(time (xt/q (xt/db xtdb-node)
      '{:find [(pull e [*])]
        :where [[e :user/aka "aka-8888"]]}))
;; "Elapsed time: 6.366125 msecs"
;; "Elapsed time: 1.437833 msecs"
;; "Elapsed time: 0.963458 msecs"
