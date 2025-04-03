(ns app.core)

(defn start []
  (let [elem (.getElementById
              js/document "app")]
    (set! (.-innerHTML elem)
          "<h1>yay</h1>")))

(defn ^:export init []
  (start))
