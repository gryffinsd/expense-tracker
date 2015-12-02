(ns expense-tracker.account.view
  (:require [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-view-helper [children path]
  [:ul.list-unstyled {:className (if (u/contains path ":") "hidden" "show")}
   (map (fn [child idx]
          (let [nm (:name child)
                href (str path ":" nm)
                grand-children (let [gc (:children child)]
                                 (when-not (empty? gc) gc))]
            ^{:key (u/random)}
            [:li {:className (if (zero? (mod idx 2)) "bg-warning" "bg-info")}
             (when grand-children
               [:a.glyph {:href "#"}
                [:span.glyphicon.glyphicon-collapse-down {:onClick toggle-ul}]])
             [:a.text-capitalize {:href "#" :onClick #(u/trans-view % {:href href})} nm]
             [:span.pull-right (:bal child)]
             (when grand-children
               [c-view-helper grand-children href])]))
        (sort-by :name children) (range))
   [:li.panel.panel-default [:span [:strong "Total"]]
    [:span.pull-right [:strong (reduce + (map :bal children))]]]])

(defn c-view-account []
  [:div.row {:id "nw"}
   [:div.col-sm-6
    [:h3 "Assets"] (c-view-helper (get-children "asset") "asset")
    [:h3 "Income"] (c-view-helper (get-children "income") "income")]
   [:div.col-sm-6
    [:h3 "Liabilities"] (c-view-helper (get-children "liability") "liability")
    [:h3 "Expenses"] (c-view-helper (get-children "expense") "expense")]])
