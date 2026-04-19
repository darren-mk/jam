(ns my-component-repl
  "com.stuartsierra.component.repl 을 직접 재구현하여 내부 동작을 이해합니다.
  원본 소스: https://github.com/stuartsierra/component.repl
  핵심 개념:
  1. system-atom  - 현재 실행 중인 시스템을 저장하는 atom
  2. init-fn      - 새 시스템을 만드는 팩토리 함수 (var로 저장)
  3. start/stop   - 시스템 생명주기 제어
  4. reset        - stop → 네임스페이스 리프레시 → start"
  (:require [clojure.tools.namespace.repl :as ns-repl]
            [com.stuartsierra.component :as component]))

;; =============================================================================
;; 1. 상태 저장소
;; =============================================================================

;; 현재 실행 중인 시스템을 하나의 atom에 보관한다.
;; nil = 시스템이 아직 시작되지 않은 상태
(defonce system-atom (atom nil))

;; init-fn을 저장하는 var. alter-var-root로 교체한다.
;; defonce가 아닌 이유: set-init! 호출 시 항상 교체되어야 하기 때문
(def ^:dynamic init-fn nil)

;; =============================================================================
;; 2. 초기화 함수 설정
;; =============================================================================

(defn set-init!
  "시스템을 새로 만드는 팩토리 함수를 등록한다.
  (set-init! #(system/make-system))
  reset을 호출할 때마다 이 함수로 새 시스템 인스턴스를 만든다."
  [f]
  ;; alter-var-root: Var의 루트 바인딩을 변경 (동적 바인딩이 아니라 루트)
  ;; 왜 atom을 쓰지 않고 var를 쓰냐?
  ;;   → Var는 네임스페이스에 속하므로 tools.namespace가 리프레시해도 살아남는다
  ;;   → (defonce var-atom ...) 도 같은 이유로 동작하지만 관용적으로 var를 사용
  (alter-var-root #'init-fn (constantly f)))

;; =============================================================================
;; 3. 시스템 생명주기
;; =============================================================================

(defn- new-system
  "init-fn을 호출해서 새 시스템 인스턴스를 반환한다."
  []
  (when (nil? init-fn)
    (throw (ex-info "init-fn이 없습니다. set-init!을 먼저 호출하세요." {})))
  (init-fn))

(defn start
  "시스템을 시작한다.
  - system-atom이 nil이면 new-system으로 새로 생성
  - component/start로 전체 시스템의 Lifecycle/start를 위상 정렬 순서로 호출"
  []
  (swap! system-atom
         (fn [current-system]
           (component/start
            (or current-system (new-system)))))
  :started)

(defn stop
  "시스템을 중단한다.

  - component/stop으로 전체 시스템의 Lifecycle/stop을 역순으로 호출
  - system-atom을 nil로 설정하여 다음 start 시 새로 생성되도록 함"
  []
  (swap! system-atom
         (fn [current-system]
           (when current-system
             (component/stop current-system))
           nil)) ;; nil로 초기화 → 다음 start 때 new-system 호출됨
  :stopped)

(defn system
  "현재 실행 중인 시스템 맵을 반환한다.

  nil이면 아직 start하지 않은 것."
  []
  @system-atom)

;; =============================================================================
;; 4. reset - component.repl의 핵심 기능
;; =============================================================================

(defn reset
  "시스템을 재시작하고 변경된 네임스페이스를 리로드한다.

  순서:
  1. stop  → 현재 시스템 종료
  2. refresh → 변경된 .clj 파일을 다시 로드 (clojure.tools.namespace)
  3. start → 새 시스템 시작

  :after 에 start를 넘기면 refresh 완료 후 자동으로 start가 호출된다.
  완전한 심볼(ns/fn)을 문자열이 아닌 심볼로 넘겨야 한다."
  []
  (stop)
  ;; ns-repl/refresh는 변경된 파일만 재로드한다 (tools.namespace 추적)
  ;; :after에 넘긴 심볼은 refresh 완료 후 resolve되어 호출된다
  ;; 왜 직접 (start)를 쓰지 않나?
  ;;   → refresh 중에 현재 네임스페이스가 언로드되므로, refresh 완료 후
  ;;     새로 로드된 심볼을 resolve해서 호출해야 안전하다
  (ns-repl/refresh :after `start))

;; =============================================================================
;; 5. 편의 함수 (원본에는 없지만 학습용으로 추가)
;; =============================================================================

(defn running?
  "시스템이 현재 실행 중인지 확인한다."
  []
  (some? @system-atom))

(defn restart
  "stop 후 start. refresh 없이 재시작할 때 사용."
  []
  (stop)
  (start))
