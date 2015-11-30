(ns expense-tracker.transaction.utils
  (:require [expense-tracker.globals :as g]
            [expense-tracker.utils :as u]
            [clojure.string :as str]))

(defn update-accounts [trans fn-1 fn-2]
  (mapv (fn [x]
          (let [{:keys [type val acc]} @x]
            (loop [accs (str/split acc ":")
                   update-path []
                   root @g/accounts]
              (when-not (empty? accs)
                (let [nm (first accs)
                      idx (u/find-index root nm :name)]
                  (swap! g/accounts update-in (conj update-path idx :bal)
                         #(if (= type :to) (fn-1 % val) (fn-2 % val)))
                  (recur (rest accs)
                         (conj update-path idx :children)
                         (:children (nth root idx))))))))
        trans))

(defn rm-helper [t]
  (update-accounts (:trans t) - +)
  (swap! g/transactions (fn [x] (remove #(= (:id t) (:id %)) x))))
