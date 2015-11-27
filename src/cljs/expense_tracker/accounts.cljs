(ns expense-tracker.accounts
  (:require [reagent.core :as r]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn gan-helper [accounts prefix rslt]
  (if (empty? accounts)
    rslt
    (let [f (first accounts)
          new-prefix (str prefix ":" (:name f))]
      (if-let [children (:children f)]
        (gan-helper (rest accounts)
                    prefix
                    (apply conj rslt new-prefix
                           (flatten (mapv #(gan-helper [%] new-prefix [])
                                          children))))
        (gan-helper (rest accounts) prefix (conj rslt new-prefix))))))

(defn accs->names [accounts]
  (into []
        (remove #(or (= % "asset") (= % "income")
                     (= % "liability") (= % "expense"))
                (map #(subs % 1) (gan-helper accounts "" [])))))
