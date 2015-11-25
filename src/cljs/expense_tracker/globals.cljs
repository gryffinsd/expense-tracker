(ns expense-tracker.globals)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; globals

(defonce acc-types [:asset :liability :income :expense])

;;;;;;;;;;;;;;;;;;;;;;;;;
;; default-values
;;    balance 0
;;    children []
;; notes
;;    children will have same :type as that of parent
(defonce accounts (atom [;; assets
                         {:name "cash"
                          :type :asset}
                         {:name "bank account"
                          :type :asset}
                         {:name "fixed deposit"
                          :type :asset}
                         ;; liabilities
                         {:name "credit card"
                          :type :liability}
                         {:name "loan"
                          :type :liability}
                         ;; income
                         {:name "salary"
                          :type :income}
                         {:name "interest"
                          :type :income}
                         {:name "gifts"
                          :type :income}
                         ;; expenses
                         {:name "groceries"
                          :type :expense}
                         {:name "vehicle"
                          :type :expense
                          :children [{:name "fuel"}
                                     {:name "repairs"}]}
                         {:name "shopping"
                          :type :expense}
                         {:name "clothes"
                          :type :expense}
                         {:name "jewellery"
                          :type :expense}
                         {:name "dining"
                          :type :expense}
                         {:name "furniture"
                          :type :expense}
                         {:name "appliances"
                          :type :expense}
                         {:name "entertainment"
                          :type :expense
                          :children [{:name "movies"}]}
                         {:name "utilities"
                          :type :expense
                          :children [{:name "electricity"}
                                     {:name "phone"}
                                     {:name "gas"}
                                     {:name "internet"}]}
                         {:name "household"
                          :type :expense
                          :children [{:name "maid"}
                                     {:name "laundry"}
                                     {:name "rent"}
                                     {:name "repairs"}]}
                         {:name "gifts"
                          :type :expense}
                         {:name "vacation"
                          :type :expense
                          :children [{:name "transport"}
                                     {:name "accomodation"}
                                     {:name "dining"}]}
                         {:name "transportation"
                          :type :expense}]))
