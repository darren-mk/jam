(ns system
  (:require [com.stuartsierra.component :as component]
            [database :as db]
            [webserver :as ws]))

;; -----------------------------------------------------------------------------
;; 시스템 팩토리 함수
;;
;; component/system-map은 컴포넌트 키와 인스턴스의 맵을 만든다.
;; component/using은 의존성을 선언한다:
;;   {:database :database}  →  컴포넌트의 :database 필드 = 시스템의 :database 키
;;   벡터 형태: [:database] → 키 이름이 같을 때 축약
;; -----------------------------------------------------------------------------

(defn make-system []
  (component/system-map
   :database (db/make-database "localhost" 5432)
   :webserver (component/using
               (ws/make-webserver 8080)
               [:database])))
