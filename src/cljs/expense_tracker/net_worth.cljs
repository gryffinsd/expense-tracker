(ns expense-tracker.net-worth
  (:require [expense-tracker.globals :as g]
            [expense-tracker.utils :as u]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn nw-helper [children]
  [:table.table.table-bordered.table-striped
   (map (fn [n]
          ^{:key (u/random)}
          [:tr [:td [:a.text-capitalize {:href (str "/trans/" (:name n))} (:name n)]]
           [:td (or (:balance n) 0)]])
        children)
   [:tr [:td [:strong "Total"]]
    [:td (reduce + (map #(or (:balance %) 0)
                        children))]]])

(defn get-children [parent]
  (:children (first (filter #(= parent (:name %)) @g/accounts))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-net-worth []
  [:div.row
   [:div.col-sm-6
    [:h3 "Assets"] (nw-helper (get-children "asset"))
    [:h3 "Income"] (nw-helper (get-children "income"))]
   [:div.col-sm-6
    [:h3 "Liabilities"] (nw-helper (get-children "liability"))
    [:h3 "Expenses"] (nw-helper (get-children "expense"))]])
