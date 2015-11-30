(ns expense-tracker.core
  (:require [reagent.core :as r]
            [expense-tracker.globals :as g]
            [expense-tracker.utils :as u]
            [expense-tracker.menu :as m]
            [expense-tracker.transaction.add :as ta]
            [expense-tracker.transaction.view :as tv]
            [expense-tracker.account.view :as av]))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn c-main []
  [:div.container [m/c-menu]
   (condp = (:page @g/app-page)
     ;; home
     :home [av/c-view-account]
     ;; transactions
     :trans-add (do (reset! ta/app-state (ta/new-state))
                    [ta/c-add-transaction])
     :trans-view [tv/c-view-transaction]
     :trans-edit (do (let [trans (first (filter #(= (:id (:attrs @g/app-page))
                                                    (:id %))
                                                @g/transactions))]
                       (tv/rm-helper trans)
                       (reset! ta/app-state trans))
                     [ta/c-add-transaction]))])

(defn main [] (r/render-component [c-main] (. js/document (getElementById "app"))))
