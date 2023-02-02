(ns malliclj.core
  (:require
   [malli.core :as m]
   [malli.instrument :as mi]
   [malli.generator :as mg]
   [clojure.spec.alpha :as s]
   [malliclj.sub :as sub]))

(defn square [x] (* x x))
(m/=> square [:=> [:cat int?] nat-int?])
(square 1.2)

(defn pass-thru [s] s)
(m/=> pass-thru [:=> [:cat int?] int?])
(pass-thru "abc")

;; has to be run after individual m-function is loaded.
(mi/instrument!)

;; once instrument is on at one place
;; it is applicable everywhere
(sub/pass-thru "xyz")

(def ttt
  [:orn
   [:nil nil?]
   [:boolean boolean?]
   [:number number?]
   [:text string?]])

(mg/generate ttt)

(def bbb
  [:or nil? boolean?])

(def p1
  (fn [x] (* x x)))

(def p2
  (m/-instrument
   {:schema [:=> [:cat :int] :int]}
   (fn [x] (* x x))))

(defn p3
  [x]
  {:pre [(s/valid? int? x)]
   :post [(s/valid? int? %)]}
  (* x x))

(time (p1 8))
(time (p2 8))
(time (p3 8))
