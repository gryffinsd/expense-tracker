(ns expense-tracker.transaction.utils
  (:require [expense-tracker.globals :as g]
            [clojure.string :as str]))

(defn find-index [haystack needle key]
  (loop [hs haystack, i 0]
    (if (= (key (first hs)) needle)
      i
      (recur (rest hs) (inc i)))))

(defn update-accounts [trans fn-1 fn-2]
  (mapv (fn [x]
          (let [{:keys [type val acc]} @x]
            (loop [accs (str/split acc ":")
                   update-path []
                   root @g/accounts]
              (when-not (empty? accs)
                (let [nm (first accs)
                      idx (find-index root nm :name)]
                  (swap! g/accounts update-in (conj update-path idx :bal)
                         #(if (= type :to) (fn-1 % val) (fn-2 % val)))
                  (recur (rest accs)
                         (conj update-path idx :children)
                         (:children (nth root idx))))))))
        trans))
