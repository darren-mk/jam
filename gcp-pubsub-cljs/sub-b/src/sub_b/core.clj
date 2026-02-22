(ns sub-b.core
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as json])
  (:import [com.google.cloud.pubsub.v1 Subscriber MessageReceiver AckReplyConsumer]
           [com.google.pubsub.v1 ProjectSubscriptionName PubsubMessage]))

(defn make-message-receiver []
  (reify MessageReceiver
    (^void receiveMessage [_ ^PubsubMessage message ^AckReplyConsumer consumer]
      (try
        (let [data-bytes (.getData message)
              json-str (.toStringUtf8 data-bytes)
              parsed-data (json/parse-string json-str true)
              message-id (.getMessageId message)]
          (log/infof "Received Message ID: %s" message-id)
          (log/infof "Content: %s" parsed-data)
          (Thread/sleep 1000)
          (.ack consumer))
        (catch Exception e
          (log/error e "Failed to process message.")
          (.nack consumer))))))

(defonce subscriber-cache
  (atom nil))

(defn start-subscriber! [project-id subscription-id]
  (if @subscriber-cache
    (log/warn "Subscriber is already running!")
    (let [subscription-name (ProjectSubscriptionName/of project-id subscription-id)
          receiver (make-message-receiver)
          subscriber (-> (Subscriber/newBuilder subscription-name receiver)
                         (.build))]
      (.startAsync subscriber)
      (.awaitRunning subscriber)
      (reset! subscriber-cache subscriber)
      (log/infof "Subscriber successfully started. Listening to: %s" subscription-id))))

(defn stop-subscriber! []
  (when-let [sub @subscriber-cache]
    (log/info "Shutting down subscriber...")
    (.stopAsync sub)
    (.awaitTerminated sub)
    (reset! subscriber-cache nil)
    (log/info "Subscriber successfully stopped.")))

(comment
  (stop-subscriber!)
  (start-subscriber!
   "<project-id>"
   "<subscription-id>"))
