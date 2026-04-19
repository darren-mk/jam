(ns redis-basics.core
  (:require [taoensso.carmine :as car]))

(def conn
  {:pool {}
   :spec {:host "127.0.0.1"
          :port 6379}})

(defmacro wcar [& body]
  `(car/wcar conn ~@body))

;;; Strings
(comment

  ;; SET and GET

  (wcar (car/set "name" "alice"))
  "OK"

  (wcar (car/get "name"))
  "alice"

  ;; SET with expiry (EX seconds)

  (wcar (car/set "session" "abc123" :ex 60))
  "OK"

  (wcar (car/ttl "session"))
  59 ; counts down; -1 = no expiry, -2 = key gone

  ;; INCR / DECR
  (wcar (car/set "counter" 0))
  "OK"

  (wcar (car/incr "counter"))
  1

  (wcar (car/get "counter"))
  "1"

  (wcar (car/incrby "counter" 5))
  6

  (wcar (car/get "counter"))
  "6")

;;; Lists
(comment
  ;; LPUSH / RPUSH / LRANGE
  (wcar (car/del "mylist"))
  0

  (wcar (car/rpush "mylist" "a" "b" "c"))
  3

  (wcar (car/lpush "mylist" "z"))
  4

  (wcar (car/lrange "mylist" 0 -1))
  ["z" "a" "b" "c"]

  ;; LPOP / RPOP
  (wcar (car/lpop "mylist"))
  "z"

  (wcar (car/rpop "mylist"))
  "c")

;;; Hashes
(comment
  ;; HSET / HGET / HGETALL
  (wcar (car/del "user:1"))
  0

  (wcar (car/hset "user:1" "name" "alice" "age" 30 "city" "NYC"))
  3

  (wcar (car/hget "user:1" "name"))
  "alice"

  (wcar (car/hget "user:1" "age"))
  "30"

  (wcar (car/hget "user:1" "city"))
  "NYC"

  (wcar (car/hgetall "user:1"))
  ["name" "alice" "age" "30" "city" "NYC"]

  ;; HINCRBY
  (wcar (car/hincrby "user:1" "age" 1))
  31)

;;; Sets
(comment

  ;; SADD / SMEMBERS / SISMEMBER
  (wcar (car/del "tags"))
  0

  (wcar (car/sadd "tags" "clojure" "redis" "functional"))
  3

  (wcar (car/smembers "tags"))
  ["clojure" "redis" "functional"]

  (wcar (car/sismember "tags" "redis"))
  1

  ;; Set operations
  (wcar (car/sadd "tags2" "redis" "java"))
  2

  (wcar (car/sinter "tags" "tags2"))
  ["redis"]

  (wcar (car/sunion "tags" "tags2"))
  ["java" "redis" "functional" "clojure"]

  (wcar (car/sdiff "tags" "tags2"))
  ["functional" "clojure"])

;;; Sorted Sets
(comment

  ;; ZADD / ZRANGE / ZSCORE

  (wcar (car/del "scores"))
  0

  (wcar (car/zadd "scores" 100 "alice" 85 "bob" 92 "carol"))
  3

  (wcar (car/zrange "scores" 0 -1 "WITHSCORES"))
  ["bob" "85" "carol" "92" "alice" "100"]

  (wcar (car/zscore "scores" "alice"))
  "100"

  ;; ZRANK / ZREVRANK

  (wcar (car/zrank "scores" "bob"))
  0

  (wcar (car/zrevrank "scores" "bob"))
  2)

;;; Transactions
(comment

  ;; MULTI/EXEC — commands inside are queued and executed atomically
  (wcar
   (car/multi)
   (car/set "a" 1)
   (car/set "b" 2)
   (car/exec))
  ["OK" "QUEUED" "QUEUED" ["OK" "OK"]]

  ;; Carmine shorthand: atomically wraps MULTI/EXEC for you
  (wcar (car/atomically
         (car/set "a" 10)
         (car/incr "a")))
  ["OK" 11])

;;; Pub/Sub
(comment

  ;; Pub/Sub uses a separate listener — not wcar
  ;; Start a subscriber in one thread:
  (def listener
    (car/with-new-pubsub-listener
      (:spec conn)
      {"channel1" (fn [[type channel message]]
                    (println type channel message))}
      (car/subscribe "channel1")))

  ;; Publish from another thread (or REPL eval):
  (wcar (car/publish "channel1" "hello"))
  1

  ;; Unsubscribe and close listener when done
  (car/close-listener listener)
  true)

;;; Key management
(comment
  (wcar (car/keys "*"))
  ["scores" "counter" "tags" "tags2" "name" "user:1"]

  (wcar (car/type "mylist"))
  "none"

  (wcar (car/exists "name"))
  1

  (wcar (car/del "name" "counter" "mylist" "user:1" "tags" "tags2" "scores"))
  6)
