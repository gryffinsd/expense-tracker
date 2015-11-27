(ns expense-tracker.net-worth
  (:require [expense-tracker.globals :as g]
            [expense-tracker.utils :as u]
            [jayq.core :as jq]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn get-children [parent]
  (:children (first (filter #(= parent (:name %)) @g/accounts))))

(defn toggle-ul [e]
  (let [ul (aget (.. e -target -parentElement -parentElement -children) 3)
        classes (.-className ul)]
    (if (u/contains classes "show")
      (set! (.-className ul) "list-unstyled hidden")
      (set! (.-className ul) "list-unstyled show"))))

(defn a-href [e href]
  (reset! g/app-page {:page :trans-view
                      :attrs {:href href}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-nw-helper [children path]
  [:ul.list-unstyled {:className (if (u/contains path ":") "hidden" "show")}
   (map (fn [child idx]
          (let [nm (:name child)
                href (str path ":" nm)
                grand-children (:children child)]
            ^{:key (u/random)}
            [:li {:className (if (zero? (mod idx 2)) "bg-warning" "bg-info")}
             (when grand-children
               [:a.glyph {:href "#"}
                [:span.glyphicon.glyphicon-collapse-down {:onClick toggle-ul}]])
             [:a.text-capitalize {:href "#" :onClick #(a-href % href)} nm]
             [:span.pull-right (or (:bal child) 0)]
             (when grand-children
               [c-nw-helper grand-children href])]))
        children (range))
   [:li.panel.panel-default [:span [:strong "Total"]]
    [:span.pull-right [:strong (reduce + (map #(or (:bal %) 0)
                                              children))]]]])

(defn c-net-worth []
  [:div.row {:id "nw"}
   [:div.col-sm-6
    [:h3 "Assets"] (c-nw-helper (get-children "asset") "asset")
    [:h3 "Income"] (c-nw-helper (get-children "income") "income")]
   [:div.col-sm-6
    [:h3 "Liabilities"] (c-nw-helper (get-children "liability") "liability")
    [:h3 "Expenses"] (c-nw-helper (get-children "expense") "expense")]])
