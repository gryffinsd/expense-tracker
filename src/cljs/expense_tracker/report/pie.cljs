(ns expense-tracker.report.pie
  (:require [reagent.core :as r]
            [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]
            [cljsjs.c3]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; local-state

(defonce app-state (r/atom nil))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn chart-generate [data]
  (when-not (empty? data)
    (.generate js/c3
               (clj->js
                {:bindto "#chart"
                 :data {:columns data #_[["data1" 30]
                                         ["data2" 120]]
                        :type :pie
                        :onclick (fn [d i]
                                   (let [id ((js->clj d) "id")]
                                     (loop [parts (str/split id #":")
                                            prefix ""
                                            root @g/accounts]
                                       (if (empty? parts)
                                         (reset! app-state
                                                 (mapv (comp #(subs % 1)
                                                             #(str prefix ":" %)
                                                             :name)
                                                       root))
                                         (recur (rest parts)
                                                (str prefix ":" (first parts))
                                                (->> root
                                                     (filter #(= (first parts) (:name %)))
                                                     first
                                                     :children))))))}
                 :pie {:label {:format (fn [value ratio id] value)}}}))))

(defn get-values [href from to]
  (mapv (fn [[k trans]]
          [(name k)
           (.abs js/Math
                 (first (mapv (comp (fn [x]
                                      ;; sum-up all sub-transactions (+ for "to", and - for "from")
                                      (reduce + (mapv #(let [a @%]
                                                         (if (= :to (:type a))
                                                           (:val a)
                                                           (- (:val a))))
                                                      x)))
                                    ;; if (or not) it is a split transaction
                                    ;; get only the sub-transaction
                                    ;; that we're interested in
                                    (fn [tr] (filter #(u/begins-with (:acc @%) k) (:trans tr))))
                              trans)))])
        ;; get all transactions for "x" b/n from and to
        (mapv (fn [x] [x (u/filter-transactions x from to)]) href)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-chart [href]
  (let [from (->> @g/app-page :attrs :from)
        to (->> @g/app-page :attrs :to)
        data (get-values @app-state from to)]
    (chart-generate data)
    [:span]))

(defn c-pie []
  (let [href (or (->> @g/app-page :attrs :href) ["income" "expense"])]
    [:div [u/c-filter-by u/rep-pie href]
     [:div {:id "chart"}
      [:div {:onClick (fn [e] (reset! app-state href))} "chart"]]
     [c-chart href]]))
