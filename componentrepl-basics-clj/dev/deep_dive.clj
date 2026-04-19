(ns deep-dive
  "component.repl 의 설계 결정들을 깊이 탐구하는 네임스페이스.

  각 실험을 comment 블록에서 직접 실행해보세요."
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :as ns-repl]
            [my-component-repl :as cr]
            [system :as sys]
            [database :as db]))

;; =============================================================================
;; 실험 1: defonce vs def - 왜 system-atom은 defonce인가?
;; =============================================================================

(comment
  ;; defonce: 이미 바인딩된 var는 재정의하지 않는다.
  ;; reset → ns-repl/refresh → 이 파일 리로드 → defonce는 atom을 새로 만들지 않음
  ;; 결과: 시스템 상태가 refresh를 거쳐도 유지된다!

  ;; 반면 def를 쓰면:
  (def test-atom (atom :original))
  (def test-atom (atom :redefined))  ;; 덮어씌워짐
  @test-atom  ;; → :redefined

  ;; defonce를 쓰면:
  (defonce persistent-atom (atom :original))
  (defonce persistent-atom (atom :redefined))  ;; 무시됨!
  @persistent-atom  ;; → :original

  )

;; =============================================================================
;; 실험 2: swap! 의 함수형 업데이트 패턴
;; =============================================================================

(comment
  ;; start에서 swap!이 하는 일을 단계별로 분해
  (def my-atom (atom nil))

  ;; nil → 새 시스템 생성 → component/start
  (swap! my-atom
         (fn [s]
           (component/start (or s (sys/make-system)))))

  ;; 이미 시작된 상태에서 다시 start하면?
  ;; (or s (new-system)) 에서 s가 non-nil이므로 new-system이 호출되지 않음
  ;; → 이미 실행 중인 시스템에 component/start를 다시 호출
  ;; → 컴포넌트 자체가 idempotent하지 않으면 문제 발생!

  )

;; =============================================================================
;; 실험 3: component/using의 의존성 주입 메커니즘
;; =============================================================================

(comment
  ;; using은 메타데이터에 의존성 정보를 붙인다
  (let [ws (component/using
            {:port 8080}  ;; 임시 맵
            [:database])]
    ;; 실제로는 :com.stuartsierra.component/dependencies 메타데이터
    (meta ws))
  ;; → {:com.stuartsierra.component/dependencies {:database :database}}

  ;; system-map이 start할 때 이 메타데이터를 읽어서
  ;; system의 :database 컴포넌트를 webserver의 :database 필드에 assoc한다

  ;; 키 매핑: 컴포넌트 필드명 → 시스템 키
  (component/using {:x nil} {:database :db})
  ;; :database 필드 ← 시스템의 :db 키 (이름이 다를 때)

  )

;; =============================================================================
;; 실험 4: tools.namespace.repl/refresh 의 동작
;; =============================================================================

(comment
  ;; refresh는 어떤 파일이 변경됐는지 어떻게 알까?
  ;; → clojure.tools.namespace.track 으로 파일 수정 시간과 ns 의존성 추적

  ;; 현재 추적 중인 네임스페이스 확인
  (ns-repl/disable-reload! *ns*)  ;; 특정 ns를 리로드 대상에서 제외

  ;; refresh-all: 변경 여부 무관하게 모든 ns 재로드
  ;; refresh: 변경된 것만 재로드 (더 빠름, 보통 이걸 씀)

  ;; :after 에 넘기는 심볼의 중요성:
  ;; (ns-repl/refresh :after 'my-component-repl/start)
  ;;                            ↑ 완전 심볼 (ns/fn)
  ;; refresh 완료 후 이 심볼을 새로 resolve해서 호출한다.
  ;; refresh 중에 현재 ns가 언로드되므로, 클로저로 캡처된 함수 참조는 stale할 수 있다.

  )

;; =============================================================================
;; 실험 5: 컴포넌트 시작/중단 순서 (위상 정렬)
;; =============================================================================

(comment
  ;; component/system-map은 의존성 그래프를 만들고
  ;; start 시: 의존성 먼저 → 의존하는 컴포넌트 나중 (위상 정렬)
  ;; stop 시:  역순

  ;; 우리 시스템의 순서:
  ;; start: database → webserver
  ;; stop:  webserver → database

  (cr/start)
  ;; 출력 순서로 위상 정렬 확인 가능

  (cr/stop)
  ;; 역순 출력 확인

  )

;; =============================================================================
;; 실험 6: 컴포넌트 레코드 vs 맵
;; =============================================================================

(comment
  ;; defrecord를 사용하는 이유:
  ;; 1. 타입 디스패치: 컴포넌트 종류를 print/inspect 시 알 수 있음
  ;; 2. 프로토콜 구현: Lifecycle/start, stop을 레코드에 직접 정의
  ;; 3. map->Record: 맵으로부터 생성하는 팩토리 함수 자동 생성

  ;; 일반 맵도 컴포넌트로 쓸 수 있다 (reify 사용)
  (def simple-component
    (reify component/Lifecycle
      (start [this]
        (println "단순 컴포넌트 시작")
        this)
      (stop [this]
        (println "단순 컴포넌트 종료")
        this)))

  (component/start simple-component)

  ;; 하지만 레코드가 더 관용적: assoc으로 필드 업데이트 가능
  ;; (start [this] (assoc this :connection ...))

  )
