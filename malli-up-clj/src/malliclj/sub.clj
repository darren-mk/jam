(ns malliclj.sub
  (:require
   [malli.core :as m]))

(defn pass-thru [s] s)
(m/=> pass-thru [:=> [:cat int?] int?])
