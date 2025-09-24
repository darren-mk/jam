(ns quartz-up-clj.core
  (:gen-class)
  (:require
   [clojure.java.io :as io])
  (:import
   (java.util Properties)
   (org.quartz.impl StdSchedulerFactory)
   (org.quartz JobBuilder TriggerBuilder
               SimpleScheduleBuilder)))

(def props (Properties.))

(defn load-props []
  (let [stream (io/input-stream
                (io/resource
                 "quartz.properties"))]
    (.load props stream)))

(def scheduler (atom nil))

(defn start-scheduler []
  (let [factory (StdSchedulerFactory.)]
    (reset! scheduler (.getScheduler factory))
    (doto @scheduler (.start))))

(defn stop-scheduler []
  (when @scheduler
    (.shutdown @scheduler)
    (println "scheduler stopped")))

(defrecord SimpleJob []
  org.quartz.Job
  (execute [_this _context]
    (println "Hello from SimpleJob!")))

(defn schedule-simple-job []
  (let [job (-> (JobBuilder/newJob SimpleJob)
                (.withIdentity "simple-job" "group1")
                (.build))
        trigger (-> (TriggerBuilder/newTrigger)
                    (.withIdentity "simple-trigger" "group1")
                    (.startNow)
                    (.withSchedule (-> (SimpleScheduleBuilder/simpleSchedule)
                                       (.withIntervalInSeconds 5)
                                       (.repeatForever)))
                    (.build))]
    (.scheduleJob @scheduler job trigger)))

(comment
  (load-props)
  (stop-scheduler)
  (start-scheduler)
  (schedule-simple-job)
  (println "Scheduler Name:"
           (.getSchedulerName
            (.getMetaData @scheduler))))

