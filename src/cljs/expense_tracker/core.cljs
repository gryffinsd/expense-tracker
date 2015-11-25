(ns expense-tracker.core
  (:require [reagent.core :as r]
            [expense-tracker.globals :as g]
            [expense-tracker.net-worth :as nw]
            [expense-tracker.utils :as u]
            [expense-tracker.menu :as m]))

(enable-console-print!)

(defonce app-state (r/atom nil))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn c-main []
  [:div.container
   [:div (m/c-menu)]
   [nw/c-net-worth]])

(defn main []
  (r/render-component [c-main]
                      (. js/document (getElementById "app"))))
