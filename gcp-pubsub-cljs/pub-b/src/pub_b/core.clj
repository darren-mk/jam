(ns pub-b.core
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as json])
  (:import [com.google.cloud.pubsub.v1 Publisher]
           [com.google.pubsub.v1 TopicName PubsubMessage]
           [com.google.protobuf ByteString]
           [com.google.api.core ApiFutureCallback ApiFutures]
           [com.google.common.util.concurrent MoreExecutors]))

(defonce publisher-cache
  (atom {}))

(defn get-publisher [project-id topic-id]
  (let [cache-key [project-id topic-id]]
    (or (get @publisher-cache cache-key)
        (let [topic-name (TopicName/of project-id topic-id)
              new-publisher (-> (Publisher/newBuilder topic-name)
                                (.build))]
          (swap! publisher-cache assoc cache-key new-publisher)
          (log/infof "Created new Publisher for topic: %s" topic-id)
          new-publisher))))

(defn- publish-callback [message-content]
  (reify ApiFutureCallback
    (onFailure [_ throwable]
      (log/error throwable "Error publishing message"))
    (onSuccess [_ message-id]
      (log/infof "Successfully published! Message ID: %s (Content: %s)"
                 message-id message-content))))

(defn publish-message! [project-id topic-id data-map]
  (let [publisher (get-publisher project-id topic-id)
        json-str (json/generate-string data-map)
        data (ByteString/copyFromUtf8 json-str)
        pubsub-message (-> (PubsubMessage/newBuilder)
                           (.setData data)
                           (.build))
        future (.publish publisher pubsub-message)]
    (ApiFutures/addCallback
     future
     (publish-callback json-str)
     (MoreExecutors/directExecutor))
    (log/infof "Queued message for publishing: %s" json-str)
    future))

(defn shutdown-publishers! []
  (doseq [[[project topic] pub] @publisher-cache]
    (log/infof "Shutting down Publisher for %s/%s..." project topic)
    (.shutdown pub))
  (reset! publisher-cache {}))

(comment
  (shutdown-publishers!)
  (for [i [1 2 3 4 5]]
    (do
      (Thread/sleep 1000)
      (publish-message!
       "<project-id>"
       "<topic-id>"
       {:msg "yo"
        :origin :pub-b
        :order i}))))
