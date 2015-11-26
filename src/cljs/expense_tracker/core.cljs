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
   [m/c-menu]
   (condp = @g/app-page
     :home [nw/c-net-worth]
     :trans-add (do (reset! tr/app-state (tr/new-state))
                    [tr/c-add-transaction]))])

(defn main [] (r/render-component [c-main] (. js/document (getElementById "app"))))
