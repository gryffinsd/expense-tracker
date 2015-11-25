(ns expense-tracker.net-worth
  (:require [expense-tracker.globals :as g]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn nw-helper [typeof]
  [:table.table.table-bordered.table-striped
   (map (fn [s]
          ^{:key (str typeof "-" (:name s))}
          [:tr [:td [:a.text-capitalize {:href (str "/trans/" (:name s))} (:name s)]]
           [:td (or (:balance s) 0)]])
        (filter #(= typeof (:type %)) @g/accounts))
   [:tr [:td [:strong "Total"]]
    [:td (reduce + (map #(or (:balance %) 0)
                        (filter #(= typeof (:type %)) @g/accounts)))]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-net-worth []
  [:div.row
   [:div.col-sm-6
    [:h3 "Assets"] (nw-helper :asset)
    [:h3 "Income"] (nw-helper :income)]
   [:div.col-sm-6
    [:h3 "Liabilities"] (nw-helper :liability)
    [:h3 "Expenses"] (nw-helper :expense)]])
