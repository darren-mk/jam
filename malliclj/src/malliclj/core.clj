(ns malliclj.core
  (:require
   [malli.core :as m]
   [malli.instrument :as mi]))

(defn square [x] (* x x))
(m/=> square [:=> [:cat int?] nat-int?])
(square 1.2)

(defn pass-thru [s] s)
(m/=> pass-thru [:=> [:cat int?] int?])
(pass-thru "abc")

;; has to be run after individual m-function is loaded.
(mi/instrument!)
