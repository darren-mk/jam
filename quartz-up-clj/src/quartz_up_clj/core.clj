(ns quartz-up-clj.core
  (:require
   [clojurewerkz.quartzite.scheduler :as qs]
   [clojurewerkz.quartzite.triggers :as qt]
   [clojurewerkz.quartzite.jobs :as qj]
   [clojurewerkz.quartzite.schedule.cron :as qsc]))

(qj/defjob NoOpJob [_ctx]
  (println "yay!"))

(comment
  (let [s (qs/start (qs/initialize))
        job (qj/build
              (qj/of-type NoOpJob)
              (qj/with-identity (qj/key "jobs.noop.1")))
        trigger (qt/build
                  (qt/with-identity (qt/key "triggers.1"))
                  (qt/start-now)
                  (qt/with-schedule (qsc/schedule
                                     (qsc/cron-schedule "0/2 * * * * ?"))))]
    (qs/schedule s job trigger)))
