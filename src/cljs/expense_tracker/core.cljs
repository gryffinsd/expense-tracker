(ns expense-tracker.core
  (:require [reagent.core :as r]
            [expense-tracker.globals :as g]
            [expense-tracker.net-worth :as nw]
            [expense-tracker.utils :as u]
            [expense-tracker.menu :as m]
            [expense-tracker.transaction.add :as ta]
            [expense-tracker.transaction.view :as tv]))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn c-main []
  [:div.container [m/c-menu]
   (condp = (:page @g/app-page)
     :home [nw/c-net-worth]
     :trans-add (do (reset! ta/app-state (ta/new-state))
                    [ta/c-add-transaction])
     :trans-view [tv/c-view-transaction])])

(defn main [] (r/render-component [c-main] (. js/document (getElementById "app"))))
