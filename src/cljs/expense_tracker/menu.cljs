(ns expense-tracker.menu
  (:require [expense-tracker.globals :as g]))

(defn c-menu []
  [:div.navbar.navbar-default
   [:div.container-fluid
    [:div.navbar-header
     [:a.navbar-brand {:href "#"} "Expense Tracker"]]
    [:div.collapse.navbar-collapse
     [:ul.nav.navbar-nav
      [:li [:a {:href "#" :onClick #(reset! g/app-page :home)} "Home"]]
      [:li [:a {:href "#" :onClick #(reset! g/app-page :trans-add)} "Add Transaction"]]
      [:li.dropdown [:a.dropdown-toggle {:href "#"
                                         :data-toggle "dropdown"
                                         :role "button"
                                         :aria-haspopup "true"
                                         :aria-expanded "false"}
                     "Accounts" [:span.caret]]
       [:ul.dropdown-menu
        [:li [:a {:href "#" :onClick #(reset! g/app-page :acc-view)} "View"]]
        [:li [:a {:href "#" :onClick #(reset! g/app-page :acc-add)} "Add New"]]]]
      [:li.dropdown [:a.dropdown-toggle {:href "#"
                                         :data-toggle "dropdown"
                                         :role "button"
                                         :aria-haspopup "true"
                                         :aria-expanded "false"}
                     "Reports by" [:span.caret]]
       [:ul.dropdown-menu
        [:li [:a {:href "#" :onClick #(reset! g/app-page :rep-pie)} "Time Period"]]
        #_[:li.divider {:role "separator"}]
        [:li [:a {:href "#" :onClick #(reset! g/app-page :rep-bar)} "Account"]]]]]]]])
