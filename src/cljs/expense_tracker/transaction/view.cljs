(ns expense-tracker.transaction.view
  (:require [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]
            [expense-tracker.transaction.utils :as tu]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn filter-transactions [needle from to]
  (let [from (or from 19700101)
        to (or to (u/m-> (u/now) u/long-fmt))]
    (loop [trans @g/transactions
           rslt []]
      (if (empty? trans)
        rslt
        (recur (rest trans)
               (let [f (first trans)
                     tr (filter #(u/contains (:acc @%) needle) (:trans f))]
                 (if (and (not (empty? tr))
                          (<= from (:date f) to))
                   (conj rslt f)
                   rslt)))))))

(defn individual-accs [haystack typeof needle]
  (->> haystack
       ((fn [x]
          (if needle
            (filter #(and (= typeof (:type @%))
                          (u/contains (:acc @%) needle))
                    x)
            (filter #(= typeof (:type @%)) x))))
       (mapv (fn [x] [(:acc @x) (:val @x)]))))

(defn ch-sel [e href]
  (let [val (.-value (.-target e))
        now (u/now)
        today (u/m-> now u/long-fmt)
        m->long (fn [arg] (u/m-> arg u/long-fmt))]
    (condp = val
      "all" (u/trans-view e {:href href :to today :from 19700101})
      "cur-mon" (u/trans-view e {:href href :to today
                                 :from (m->long (.date now 1))})
      "last-30" (u/trans-view e {:href href :to today
                                 :from (m->long (.subtract now 30 "days"))})
      "last-60" (u/trans-view e {:href href :to today
                                 :from (m->long (.subtract now 60 "days"))})
      "last-90" (u/trans-view e {:href href :to today
                                 :from (m->long (.subtract now 90 "days"))})
      "cur-yr" (u/trans-view e {:href href :to today
                                :from (m->long (.month (.date now 1) 0))})
      "prev-mon" (let [prev (.subtract now 1 "months")]
                   (u/trans-view e {:href href
                                    :to (m->long (.date prev 31))
                                    :from (m->long (.date prev 1))}))
      "prev-yr" (let [prev (.subtract now 1 "years")]
                  (u/trans-view e {:href href
                                   :to (m->long (.month (.date prev 31) 12))
                                   :from (m->long (.month (.date prev 1) 0))}))
      "custom" (u/trans-view e {:href href}))))

(defn rm-helper [t]
  (tu/update-accounts (:trans t) + -)
  (swap! g/transactions (fn [x] (remove #(= (:id t) (:id %)) x))))

(defn rm [e t]
  (when (u/confirm "Do you really want to delete this transaction?")
    (rm-helper t)
    (u/trans-view nil (:attrs @g/app-page))))

(defn edit [e t] (reset! g/app-page {:page :trans-edit :attrs {:id (:id t)}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-acc [[href _]]
  [:a {:href "#" :onClick #(u/trans-view % {:href href}) :style {:margin-right "1em"}} href])

(defn c-view-transaction []
  (let [href (:href (:attrs @g/app-page))
        from (->> @g/app-page :attrs :from)
        to (->> @g/app-page :attrs :to)
        fltr (filter-transactions href from to)]
    [:div [:p [:label "Filter-by"]
           [:select {:onChange #(ch-sel % href)}
            [:option {:value "all"} "All"]
            [:option {:value "cur-mon"} "Current Month"]
            [:option {:value "prev-mon"} "Previous Month"]
            [:option {:value "last-30"} "Last 30 days"]
            [:option {:value "last-60"} "Last 60 days"]
            [:option {:value "last-90"} "Last 90 days"]
            [:option {:value "cur-yr"} "Current Year"]
            [:option {:value "prev-yr"} "Previous Year"]
            #_[:option {:value "custom"} "Custom"]]]
     [:table.table.table-striped.table-bordered
      [:tbody [:tr [:th "Date"] [:th "From"] [:th "To"] [:th "Amount"] [:th "Manage"]]
       (for [f fltr]
         (let [from (let [rslt (individual-accs (:trans f) :from href)]
                      (if-not (empty? rslt)
                        rslt
                        (individual-accs (:trans f) :from nil)))
               to (let [rslt (individual-accs (:trans f) :to href)]
                    (if-not (empty? rslt)
                      rslt
                      (individual-accs (:trans f) :to nil)))]
           ^{:key (u/random)}
           [:tr [:td (:date f)]
            [:td (for [ia from] ^{:key (u/random)} [c-acc ia])]
            [:td (for [ia to] ^{:key (u/random)} [c-acc ia])]
            [:td (if (or (= (count from) 1) (= (count to) 1))
                   (if (= (count from) 1)
                     (second (first from))
                     (second (first to)))
                   (:to f))]
            [:td [:a.glyph {:href "#"} [:span.glyphicon.glyphicon-pencil {:onClick #(edit % f)}]]
             [:a.glyph {:href "#"} [:span.glyphicon.glyphicon-remove {:onClick #(rm % f)}]]]]))]]]))
