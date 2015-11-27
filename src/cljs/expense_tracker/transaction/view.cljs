(ns expense-tracker.transaction.view
  (:require [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]
            [expense-tracker.accounts :as a]
            [clojure.string :as string]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn filter-transactions [needle]
  (loop [trans @g/transactions
         rslt []]
    (if (empty? trans)
      rslt
      (recur (rest trans)
             (let [f (first trans)
                   tr (filter #(u/contains (:acc @%) needle) (:trans f))]
               (if-not (empty? tr)
                 (conj rslt f)
                 rslt))))))

(defn individual-accs [haystack typeof needle]
  (->> haystack
       ((fn [x]
          (if needle
            (filter #(and (= typeof (:type @%))
                          (u/contains (:acc @%) needle))
                    x)
            (filter #(= typeof (:type @%)) x))))
       (mapv (fn [x] [(:acc @x) (:val @x)]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-acc [[href _]]
  (letfn [(a-href [e]
            (reset! g/app-page {:page :trans-view
                                :attrs {:href href}}))]
    [:a {:href "#" :onClick a-href :style {:margin-right "1em"}} href]))

(defn c-view-transaction []
  (let [href (:href (:attrs @g/app-page))
        fltrd (filter-transactions href)]
    [:table.table.table-striped.table-bordered
     [:tbody [:tr [:th "Date"] [:th "From"] [:th "To"] [:th "Amount"]]
      (for [f fltrd]
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
                  (:to f))]]))]]))
