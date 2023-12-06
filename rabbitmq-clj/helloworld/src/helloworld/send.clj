(ns helloworld.send
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.basic :as lb]))

(defn send-msg [msg]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)
          queue-name "hello"]
      (lq/declare
       ch queue-name
       {:durable false
        :auto-delete false})
      (lb/publish
       ch "" queue-name
       (.getBytes msg "UTF-8"))
      (println " [x] Sent 'Hello World!'"))))

(comment
  (send-msg "yo, first")
  (send-msg "hey, second")
  (send-msg "mate, third"))
