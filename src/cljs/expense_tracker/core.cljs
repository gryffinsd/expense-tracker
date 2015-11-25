(ns expense-tracker.core
  (:require [reagent.core :as r]
            [expense-tracker.globals :as g]
            [expense-tracker.net-worth :as nw]
            [expense-tracker.transaction :as tr]
            [expense-tracker.utils :as u]
            [expense-tracker.menu :as m]))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn c-main []
  [:div.container
   [:div (m/c-menu)]
   [tr/c-add-transaction]
   #_[nw/c-net-worth]])

(defn main []
  (r/render-component [c-main]
                      (. js/document (getElementById "app"))))
