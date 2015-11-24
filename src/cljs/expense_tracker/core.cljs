(ns expense-tracker.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(defonce app-state (r/atom {:text "Hello Chestnut!"}))

(defn c-main []
  [:h1 (:text @app-state)])

(defn main []
  (r/render-component [c-main]
                      (. js/document (getElementById "app"))))
