(ns expense-tracker.core
  (:require [reagent.core :as r]
            [expense-tracker.globals :as g]
            [expense-tracker.menu :as m]))

(enable-console-print!)

(defonce global-state (atom nil))
(defonce app-state (r/atom {:to 0, :from 0}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; utils

(defn reset [] (reset! app-state nil))
(defn log [& args] (.log js/console (apply str args)))
(defn alert [& args] (js/alert (apply str args)))
(defn contains [haystack needle] (when haystack (>= (.indexOf haystack needle) 0)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DOM utils

(defn by-id [id] (.getElementById js/document id))
(defn create-element [typeof classes attrs & inner]
  (let [ele (.createElement js/document typeof)]
    (.setAttribute ele "class" classes)
    (mapv (fn [[k v]] (.setAttribute ele k v)) attrs)
    (when inner (set! (.-innerHTML ele) (first inner)))
    ele))
(defn append-child [parent child]
  (.appendChild parent child))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn nw-helper [typeof]
  [:table.table.table-bordered.table-striped
   (map (fn [s]
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn c-main []
  [:div.container
   [:div (m/c-menu)]
   [c-net-worth]])

(defn main []
  (r/render-component [c-main]
                      (. js/document (getElementById "app"))))
