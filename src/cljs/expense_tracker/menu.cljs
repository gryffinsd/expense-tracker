(ns expense-tracker.menu)

(defn c-menu []
  [:div.navbar.navbar-default
   [:div.navbar-header
    [:a.navbar-brand {:href "/"} "Gryffin -- Expense Tracker"]
    [:div.btn-group
     [:ul.nav.navbar-nav
      [:li [:a {:href "/"} "Home"]]
      [:li "Accounts"
       [:ul
        [:li [:a {:href "/acc/add"} "Add"]]
        [:li [:a {:href "/acc/edit"} "Edit"]]
        [:li [:a {:href "/acc/del"} "Delete"]]]]
      [:li "Reports"
       [:ul
        [:li [:a {:href "/rep/nw"} "Net Worth"]]
        [:li [:a {:href "/rep/tim"} "Time-Periods"]]]]]]]])
