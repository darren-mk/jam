(ns typedupclj.model)

(def address
  [:map
   [:street string?]
   [:city string?]
   [:zip int?]])

(def address-subset
  [:map
   [:street string?]
   [:city string?]])
