(ns user
  "REPL 시작 시 자동으로 로드되는 개발용 네임스페이스.
  사용법:
    (start)   → 시스템 시작
    (stop)    → 시스템 종료
    (reset)   → 코드 변경 후 재시작 (핵심!)
    (system)  → 현재 시스템 상태 조회"
  (:require [my-component-repl :refer [set-init! start stop reset system running?]]
            [system :as sys]
            [database :as db]
            [webserver :as ws]))

;; =============================================================================
;; 시스템 초기화 함수 등록
;; =============================================================================

;; REPL 시작 시 한 번만 set-init! 을 호출하면 된다.
;; reset을 호출할 때마다 이 함수로 새 시스템을 만든다.
(set-init! #(sys/make-system))

;; =============================================================================
;; REPL 실습 시나리오
;; =============================================================================

(comment
  ;; ── 기본 사용 ──────────────────────────────────────────────────────────────

  ;; 1. 시스템 시작
  (start)
  ;; [Database] 연결 시작: localhost:5432
  ;; [Scheduler] 시작 (DB에 의존)
  ;; [WebServer] 포트 8080에서 시작

  ;; 2. 현재 시스템 상태 확인
  (system)
  (keys (system))  ;; → (:database :webserver)

  ;; 3. 특정 컴포넌트에 직접 접근
  (get (system) :database)
  (get-in (system) [:database :connection])
  (get-in (system) [:webserver :port])

  ;; 4. 컴포넌트 함수 호출
  (db/query (get (system) :database) "SELECT * FROM users")
  (ws/handle-request (get (system) :webserver) "/api/users")

  ;; 5. 의존성 주입 확인: webserver 안에 database가 들어있는지 확인
  (get-in (system) [:webserver :database :connection])  ;; nil이 아니어야 함

  ;; ── 시스템 종료 ───────────────────────────────────────────────────────────

  (stop)
  ;; [WebServer] 종료
  ;; [Scheduler] 종료  (역순!)
  ;; [Database] 연결 종료

  (system)    ;; → nil
  (running?)  ;; → false

  ;; ── reset 흐름 이해하기 ───────────────────────────────────────────────────

  ;; 코드를 수정한 후 reset을 호출하면:
  ;; 1. (stop) 현재 시스템 종료
  ;; 2. tools.namespace가 변경된 .clj 파일 감지 후 재로드
  ;; 3. (start) 새 시스템 시작
  (reset)

  ;; ── 컴포넌트 의존성 그래프 확인 ──────────────────────────────────────────

  ;; component/system-map 내부를 보면 의존성 정보가 들어있다
  (let [s (sys/make-system)]
    ;; com.stuartsierra.dependency 그래프
    (.dependencies s))

  ;; ── system-atom 직접 들여다보기 ──────────────────────────────────────────

  ;; my-component-repl 네임스페이스의 atom을 직접 볼 수 있다
  (deref my-component-repl/system-atom))
