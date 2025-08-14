(ns app.core
  (:require
   ["react" :as react]
   ["react-table" :as rt]
   [reagent.core :as r]
   [reagent.dom.client :as rdc]))

(def root-elem
  (.getElementById
   js/document
   "app"))

(defonce boxed-root
  (atom nil))

(def columns
  [{:Header "Name"
    :columns [{:Header "First Name" :accessor "firstName"}
              {:Header "Last Name"  :accessor "lastName"}]}
   {:Header "Info"
    :columns [{:Header "Age"              :accessor "age"}
              {:Header "Visits"           :accessor "visits"}
              {:Header "Status"           :accessor "status"}
              {:Header "Profile Progress" :accessor "progress"}]}])

(def data
  [{:firstName "Jane" :lastName "Doe" :age 20
    :visits 15 :status "single" :progress 50}
   {:firstName "John" :lastName "Smith" :age 21
    :visits 22 :status "complicated" :progress 70}
   {:firstName "Ada" :lastName "Lovelace" :age 28
    :visits 5 :status "single" :progress 95}])

(defn Table [{:keys [columns data]}]
  (let [;; Make the JS objects referentially stable across renders
        cols (react/useMemo (fn [] (clj->js columns)) #js [columns])
        dat  (react/useMemo (fn [] (clj->js data))    #js [data])
        dcol (react/useMemo (fn [] #js {:minWidth 50 :width 150 :maxWidth 500}) #js [])
        inst (rt/useTable #js {:columns cols :data dat :defaultColumn dcol}
                          rt/useBlockLayout
                          rt/useResizeColumns)
        getTableProps (.-getTableProps inst)
        getTableBodyProps (.-getTableBodyProps inst)
        headerGroups (array-seq (.-headerGroups inst))
        rows (array-seq (.-rows inst))
        prepareRow (.-prepareRow inst)]
    [:div.table (js->clj (getTableProps) :keywordize-keys true)
     [:div.thead
      (for [hg headerGroups]
        ^{:key (.-id hg)}
        [:div.tr (js->clj (.getHeaderGroupProps hg) :keywordize-keys true)
         (for [col (array-seq (.-headers hg))]
           ^{:key (.-id col)}
           [:div.th (js->clj (.getHeaderProps col) :keywordize-keys true)
            (.render col "Header")
            (when (.-canResize col)
              (let [rp (js->clj (.getResizerProps col) :keywordize-keys true)
                    cls (str (when-let [c (:class rp)] (str c " "))
                             "resizer" (when (.-isResizing col) " isResizing"))]
                [:div (assoc rp :class cls)]))])])]
     [:div.tbody (js->clj (getTableBodyProps) :keywordize-keys true)
      (for [row rows]
        (do (prepareRow row)
            ^{:key (.-id row)}
            [:div.tr (js->clj (.getRowProps row) :keywordize-keys true)
             (for [cell (array-seq (.-cells row))]
               ^{:key (.-id (.-column cell))}
               [:div.td (js->clj (.getCellProps cell) :keywordize-keys true)
                (.render cell "Cell")])]))]]))

(defn view []
  [:div
   [:h1 "yo!"]
   [:f> Table {:columns columns :data data}]])

(defn ^:dev/after-load start []
  (when-not @boxed-root
    (reset! boxed-root
            (rdc/create-root root-elem)))
  (.render @boxed-root
           (r/as-element [view])))

(defn ^:export init []
  (start))