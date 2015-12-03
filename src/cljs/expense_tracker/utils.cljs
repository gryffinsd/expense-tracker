(ns expense-tracker.utils
  (:require [expense-tracker.globals :as g]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; utils

(defn reset [as] (reset! as nil))
(defn log [& args] (.log js/console (apply str args)))
(defn alert [& args] (js/alert (apply str args)) nil)
(defn confirm [& args] (js/confirm (apply str args)))

(defn contains [haystack needle] (when haystack (>= (.indexOf haystack needle) 0)))
(defn begins-with [haystack needle] (when haystack (zero? (.indexOf haystack needle))))
(defn random [] (.random js/Math))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DOM utils

(defn by-id [id] (.getElementById js/document id))
(defn create-element [typeof classes attrs & inner]
  (let [ele (.createElement js/document typeof)]
    (.setAttribute ele "class" classes)
    (mapv (fn [[k v]] (.setAttribute ele k v)) attrs)
    (when inner (set! (.-innerHTML ele) (first inner)))
    ele))
(defn append-child [parent child]
  (.appendChild parent child))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; moment.js

(def local-fmt "DD-MON-YYYY")
(def long-fmt "YYYYMMDD")
(def jq-fmt "MM/DD/YYYY")
(defn now [] (.moment js/window))
(defn ->m [arg fmt] (.moment js/window arg fmt))
(defn m-> [arg fmt] (.format arg fmt))
(defn jq->long [arg] (-> arg (->m jq-fmt) (m-> long-fmt)))
(defn long->local [arg] (-> arg (->m long-fmt) (m-> local-fmt)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; misc

(defn trans-view [_ attrs]
  (swap! g/app-page conj {:page :trans-view :attrs attrs}))

(defn rep-pie [_ attrs]
  (swap! g/app-page conj {:page :rep-pie :attrs attrs}))

(defn find-index [haystack needle key]
  (loop [hs haystack, i 0]
    (if (= (key (first hs)) needle)
      i
      (recur (rest hs) (inc i)))))

(defn filter-transactions [needle from to]
  (let [from (or from 19700101)
        to (or to (m-> (now) long-fmt))]
    (loop [trans @g/transactions
           rslt []]
      (if (empty? trans)
        rslt
        (recur (rest trans)
               (let [f (first trans)
                     tr (filter #(begins-with (:acc @%) needle) (:trans f))]
                 (if (and (not (empty? tr))
                          (<= from (:date f) to))
                   (conj rslt f)
                   rslt)))))))

(defn trim-empty? [app-state val key]
  (let [x (str/trim val)]
    (if-not (empty? x) x (key @app-state))))

(defn amt-validate? [val]
  (let [f (js/parseFloat val)
        i (js/parseInt val)]
    (if (or (= val "") ; empty string
            (= i f) ; ending w/ decimal point
            (= (str f) val))
      f
      nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-filter-by [f href]
  (letfn [(ch-sel [e]
            (let [val (.-value (.-target e))
                  n (now)
                  today (m-> n long-fmt)
                  m->long (fn [arg] (m-> arg long-fmt))]
              (condp = val
                "all" (f e {:href href :to today :from 19700101})
                "cur-mon" (f e {:href href :to today
                                :from (m->long (.date n 1))})
                "last-30" (f e {:href href :to today
                                :from (m->long (.subtract n 30 "days"))})
                "last-60" (f e {:href href :to today
                                :from (m->long (.subtract n 60 "days"))})
                "last-90" (f e {:href href :to today
                                :from (m->long (.subtract n 90 "days"))})
                "cur-yr" (f e {:href href :to today
                               :from (m->long (.month (.date n 1) 0))})
                ;; .subtract/.date mutates now/prev :((
                "prev-mon" (let [prev-to (.subtract (now) 1 "months")
                                 prev-from (.subtract (now) 1 "months")]
                             (f e {:href href
                                   :to (m->long (.date prev-to (.daysInMonth prev-to)))
                                   :from (m->long (.date prev-from 1))}))
                "prev-yr" (let [prev-to (.subtract (now) 1 "years")
                                prev-from (.subtract (now) 1 "years")]
                            (f e {:href href
                                  :to (m->long (.date (.month prev-to 11) 31))
                                  :from (m->long (.date (.month prev-from 0) 1))}))
                "custom" (f e {:href href}))))]
    [:div.form-group [:label "Filter-by: "]
     [:select.form-control {:onChange ch-sel}
      [:option {:value "all"} "All"]
      [:option {:value "cur-mon"} "Current Month"]
      [:option {:value "prev-mon"} "Previous Month"]
      [:option {:value "last-30"} "Last 30 days"]
      [:option {:value "last-60"} "Last 60 days"]
      [:option {:value "last-90"} "Last 90 days"]
      [:option {:value "cur-yr"} "Current Year"]
      [:option {:value "prev-yr"} "Previous Year"]
      #_[:option {:value "custom"} "Custom"]]]))
