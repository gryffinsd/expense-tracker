(ns expense-tracker.transaction.utils
  (:require [expense-tracker.globals :as g]
            [expense-tracker.utils :as u]
            [clojure.string :as str]))

(defn update-accounts [trans fn-1 fn-2 & [acc-name]]
  (mapv (fn [x]
          (let [{:keys [type val acc]} @x]
            (loop [acc-parts (str/split acc ":")
                   update-path []
                   root @g/accounts]
              (when-not (empty? acc-parts)
                (let [nm (first acc-parts)
                      idx (u/find-index root nm :name)]
                  (when (or (not acc-name) (= acc-name acc))
                    (swap! g/accounts update-in (conj update-path idx :bal)
                           #(if (= type :to) (fn-1 % val) (fn-2 % val))))
                  (recur (rest acc-parts)
                         (conj update-path idx :children)
                         (:children (nth root idx))))))))
        trans))

(defn filter-transactions [acc]
  (filter (fn [trans] (->> trans
                           :trans
                           (filter #(= acc (:acc @%)))
                           empty?
                           not))
          @g/transactions))

(defn replay [acc]
  (update-accounts (mapcat :trans (filter-transactions acc)) + - acc))

(defn rename-acc [old new]
  (mapv (fn [gt]
          (mapv #(when (= (:acc @%) old) (swap! % assoc :acc new))
                (:trans gt)))
        @g/transactions))

(defn rm-helper [t]
  (update-accounts (:trans t) - +)
  (swap! g/transactions (fn [x] (remove #(= (:id t) (:id %)) x))))
