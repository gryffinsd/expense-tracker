(ns expense-tracker.account.rm
  (:require [expense-tracker.globals :as g]
            [expense-tracker.utils :as u]
            [expense-tracker.transaction.utils :as tu]
            [expense-tracker.account.utils :as au]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn rm-trans [acc]
  (mapv tu/rm-helper (tu/filter-transactions acc)))

(defn rm-acc [acc]
  (let [accs (str/split acc #":")
        indices (au/accs->indices (drop-last 1 accs))]
    (swap! g/accounts
           update-in indices
           #(vec (remove (fn [x] (= (last accs) (:name x))) %)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn rm [e]
  (when (u/confirm "Do you really want to delete this account?")
    (when (u/confirm "This action will delete *all* transactions in this account!
Do you still want to continue?")
      (let [acc (:href (:attrs @g/app-page))]
        (rm-trans acc)
        (rm-acc acc))
      (reset! g/app-page {:page :home}))))
