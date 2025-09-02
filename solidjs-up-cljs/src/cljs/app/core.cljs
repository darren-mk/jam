(ns app.core
  (:require
   ["solid-js/web/dist/web.js" :as sw]))

(defn view []
  (doto (.createElement js/document "h1")
    (aset "textContent" "yay!")))

(defonce dispose! (atom nil))

(defn mount! []
  ;; dispose previous tree on hot-reload
  (when-let [d @dispose!] (d))
  (let [root (.getElementById js/document "app")]
    (reset! dispose! (sw/render view root))))

(defn ^:export init []
  (mount!))

;; shadow-cljs hot-reload hook
(defn ^:dev/after-load reload []
  (mount!))
