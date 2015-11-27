(ns expense-tracker.menu
  (:require [expense-tracker.globals :as g]))

;; reset-app
(defn ra [e m] (reset! g/app-page m) nil)

(defn c-menu []
  [:div.navbar.navbar-default
   [:div.container-fluid
    [:div.navbar-header
     [:a.navbar-brand {:href "#"} "Expense Tracker"]]
    [:div.collapse.navbar-collapse
     [:ul.nav.navbar-nav
      [:li [:a {:href "#" :onClick #(ra % {:page :home})} "Home"]]
      [:li [:a {:href "#" :onClick #(ra % {:page :trans-add})} "Add Transaction"]]
      [:li.dropdown [:a.dropdown-toggle {:href "#"
                                         :data-toggle "dropdown"
                                         :role "button"
                                         :aria-haspopup "true"
                                         :aria-expanded "false"}
                     "Accounts" [:span.caret]]
       [:ul.dropdown-menu
        [:li [:a {:href "#" :onClick #(ra % {:page :acc-view})} "View"]]
        [:li [:a {:href "#" :onClick #(ra % {:page :acc-add})} "Add New"]]]]
      [:li.dropdown [:a.dropdown-toggle {:href "#"
                                         :data-toggle "dropdown"
                                         :role "button"
                                         :aria-haspopup "true"
                                         :aria-expanded "false"}
                     "Reports by" [:span.caret]]
       [:ul.dropdown-menu
        [:li [:a {:href "#" :onClick #(ra % {:page :rep-pie})} "Time Period"]]
        #_[:li.divider {:role "separator"}]
        [:li [:a {:href "#" :onClick #(ra % {:page :rep-bar})} "Account"]]]]]]]])
