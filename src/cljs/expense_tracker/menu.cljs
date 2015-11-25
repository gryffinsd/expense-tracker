(ns expense-tracker.menu
  (:require [expense-tracker.globals :as g]))

(defn c-menu []
  [:div.navbar.navbar-default
   [:div.navbar-header
    [:a.navbar-brand {:href "#"} "Gryffin -- Expense Tracker"]
    [:div.btn-group
     [:ul
      [:li [:a {:href "#" :onClick #(reset! g/app-page :home)} "Home"]]
      [:li "Transactions"
       [:ul
        [:li "View"]
        [:li [:a {:href "#" :onClick #(reset! g/app-page :trans-add)} "Add"]] ;
        [:li "Edit"]
        [:li "Delete"]]]
      [:li "Accounts"
       [:ul
        [:li "Add"]
        [:li "Edit"]
        [:li "Delete"]]]
      [:li "Reports"
       [:ul
        [:li "Net Worth"]
        [:li "Time-Periods"]]]]]]])
