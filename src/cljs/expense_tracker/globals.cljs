(ns expense-tracker.globals
  (:require [reagent.core :as r]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn normalize-name [name]
  (str/replace name " " "-"))

(defn gan-helper [accounts prefix rslt]
  (if (empty? accounts)
    rslt
    (let [f (first accounts)
          new-prefix (str prefix ":" (normalize-name (:name f)))]
      (if-let [children (:children f)]
        (gan-helper (rest accounts)
                       prefix
                       (apply conj rslt new-prefix
                              (flatten (mapv #(gan-helper [%] new-prefix [])
                                             children))))
        (gan-helper (rest accounts) prefix (conj rslt new-prefix))))))

(defn gen-acc-names [accounts]
  (mapv #(subs % 1) (gan-helper accounts "" [])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; globals

(defonce app-page (r/atom :home))
(defonce transactions (atom []))

;; default-values: balance 0, children []
(defonce accounts (atom [{:name "asset"
                          :children [{:name "cash"}
                                     {:name "bank account"}
                                     {:name "fixed deposit"}]}
                         {:name "liability"
                          :children [{:name "credit card"}
                                     {:name "loan"}]}
                         {:name "income"
                          :children [{:name "salary"}
                                     {:name "interest"}
                                     {:name "gifts"}]}
                         {:name "expense"
                          :children [{:name "groceries"}
                                     {:name "vehicle"
                                      :children [{:name "fuel"}
                                                 {:name "repairs"}]}
                                     {:name "shopping"}
                                     {:name "clothes"}
                                     {:name "jewellery"}
                                     {:name "dining"}
                                     {:name "furniture"}
                                     {:name "appliances"}
                                     {:name "entertainment"
                                      :children [{:name "movies"}]}
                                     {:name "utilities"
                                      :children [{:name "electricity"}
                                                 {:name "phone"}
                                                 {:name "gas"}
                                                 {:name "internet"}]}
                                     {:name "household"
                                      :children [{:name "maid"}
                                                 {:name "laundry"}
                                                 {:name "rent"}
                                                 {:name "repairs"}]}
                                     {:name "gifts"}
                                     {:name "vacation"
                                      :children [{:name "transportation"}
                                                 {:name "accomodation"}
                                                 {:name "dining"}]}
                                     {:name "transportation"}]}]))
(defonce account-names (atom (gen-acc-names @accounts)))
