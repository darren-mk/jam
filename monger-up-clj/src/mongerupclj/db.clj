(ns mongerupclj.db
  (:require [monger.core :as mg]))

;; 공통 DB 연결 — 다른 네임스페이스에서 require 해서 씁니다

(def conn (mg/connect))
(def db   (mg/get-db conn "mongofundamentals"))
