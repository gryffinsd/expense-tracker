(ns expense-tracker.core
  (:require [reagent.core :as r]
            [expense-tracker.globals :as g]
            [expense-tracker.menu :as m]
            [expense-tracker.transaction.add :as ta]
            [expense-tracker.transaction.view :as tv]
            [expense-tracker.transaction.utils :as tu]
            [expense-tracker.account.view :as av]
            [expense-tracker.account.manage :as am]
            [expense-tracker.report.pie :as rp]
            [clojure.string :as str]))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn c-main []
  [:div.container [m/c-menu]
   (condp = (:page @g/app-page)
     ;; home
     :home [av/c-view-account]
     ;; accounts
     :acc-add (do (reset! am/app-state (am/new-state nil "" nil))
                  [am/c-add])
     :acc-edit (let [href (-> @g/app-page :attrs :href)
                     nm (-> href (str/split #":") last)
                     parent (->> href
                                 (#(str/split % #":"))
                                 (drop-last 1)
                                 (str/join ":"))]
                 (reset! am/app-state (am/new-state true nm parent))
                 [am/c-add])
     ;; transactions
     :trans-add (do (reset! ta/app-state (ta/new-state))
                    [ta/c-add])
     :trans-view [tv/c-view]
     :trans-edit (do (let [trans (first (filter #(= (:id (:attrs @g/app-page))
                                                    (:id %))
                                                @g/transactions))]
                       (tu/rm-helper trans)
                       (reset! ta/app-state trans))
                     [ta/c-add])
     ;; reports
     :rep-pie [rp/c-pie])])

(defn main [] (r/render-component [c-main] (. js/document (getElementById "app"))))
