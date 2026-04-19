(ns database
  (:require [com.stuartsierra.component :as component]))

;; -----------------------------------------------------------------------------
;; 가짜 DB 컴포넌트 - 실제 연결 대신 atom으로 시뮬레이션
;; -----------------------------------------------------------------------------

(defrecord Database [host port connection]
  component/Lifecycle
  (start [this]
    (println (str "[Database] 연결 시작: " host ":" port))
    (let [conn {:host host :port port :pool (atom [])}]
      (assoc this :connection conn)))
  (stop [this]
    (println "[Database] 연결 종료")
    (assoc this :connection nil)))

(defn make-database [host port]
  (map->Database {:host host :port port :connection nil}))

;; REPL에서 직접 테스트할 수 있는 헬퍼들
(defn query [db sql]
  (if (:connection db)
    (do (println (str "[Database] 쿼리 실행: " sql))
        {:rows [] :sql sql})
    (throw (ex-info "DB가 시작되지 않았습니다" {:db db}))))
