(ns expense-tracker.utils
  (:require [expense-tracker.globals :as g]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; utils

(defn reset [as] (reset! as nil))
(defn log [& args] (.log js/console (apply str args)))
(defn alert [& args] (js/alert (apply str args)) nil)
(defn confirm [& args] (js/confirm (apply str args)))

(defn contains [haystack needle] (when haystack (>= (.indexOf haystack needle) 0)))
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
  (reset! g/app-page {:page :trans-view :attrs attrs}))

(defn find-index [haystack needle key]
  (loop [hs haystack, i 0]
    (if (= (key (first hs)) needle)
      i
      (recur (rest hs) (inc i)))))
