(ns expense-tracker.utils)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; utils

(defn reset [as] (reset! as nil))
(defn log [& args] (.log js/console (apply str args)))
(defn alert [& args] (js/alert (apply str args)))
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
