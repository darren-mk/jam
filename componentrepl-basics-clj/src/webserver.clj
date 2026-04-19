(ns webserver
  (:require [com.stuartsierra.component :as component]))

;; -----------------------------------------------------------------------------
;; 웹서버 컴포넌트 - DB에 의존
;; -----------------------------------------------------------------------------

(defrecord WebServer [port database server]
  component/Lifecycle
  (start [this]
    (println (str "[WebServer] 포트 " port "에서 시작"))
    ;; 의존 컴포넌트들이 정상적으로 시작됐는지 확인
    (when-not (:connection database)
      (throw (ex-info "WebServer: DB가 먼저 시작되어야 합니다" {})))
    (let [fake-server {:port port :requests (atom 0)}]
      (assoc this :server fake-server)))
  (stop [this]
    (println "[WebServer] 종료")
    (assoc this :server nil)))

(defn make-webserver [port]
  (map->WebServer {:port port}))

;; 서버 상태 조회 헬퍼
(defn handle-request [ws path]
  (if (:server ws)
    (do (swap! (get-in ws [:server :requests]) inc)
        {:status 200 :path path :db-connected? (some? (:connection (:database ws)))})
    (throw (ex-info "서버가 시작되지 않았습니다" {}))))
