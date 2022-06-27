(ns integrantclj.core
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]))

(def config
  (ig/read-string
   (slurp
    (io/file "resources/config.edn"))))

(defmethod ig/init-key :favorites/jane [_ opts]
  (let [{:keys [flower]} opts]
    (str "a bunch of " flower)))

(defmethod ig/init-key :pleasing/jane [_ opts]
  (let [{:keys [time gift]} opts]
    (str "at " (name time)
         " give her " gift)))

(ig/init config)
;; => {:favorites/jane "a bunch of freesia",
;;     :pleasing/jane "at noon give her a bunch of freesia"}
