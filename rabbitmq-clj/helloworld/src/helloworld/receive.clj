(ns helloworld.receive
  (:require
   [langohr.core :as lc]
   [langohr.channel :as lch]
   [langohr.queue :as lq]
   [langohr.consumers :as lcons]))

(defn handle-delivery
  "Handles message delivery"
  [_ch _metadata payload]
  (println (format " [x] Received %s" (String. payload "UTF-8"))))

(defn consume-msg []
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lq/declare ch "hello" {:durable false :auto-delete false})
      (println " [*] Waiting for messages. To exit press CTRL+C")
      (lcons/blocking-subscribe ch "hello" handle-delivery {:auto-ack true}))))

(comment
  (consume-msg)
  ;; [x] Received yo, first
  ;; [x] Received hey, second
  ;; [x] Received mate, third
  )
