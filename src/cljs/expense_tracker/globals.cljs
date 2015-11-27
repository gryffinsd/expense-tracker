(ns expense-tracker.globals
  (:require [reagent.core :as r]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; global states

(defonce app-page (r/atom {:page :home
                           :attrs nil}))
(defonce transactions (atom []))
(defonce accounts (atom [{:name "asset" :bal 0
                          :children [{:name "cash" :bal 0}
                                     {:name "bank account" :bal 0}
                                     {:name "fixed deposit" :bal 0}]}
                         {:name "liability" :bal 0
                          :children [{:name "credit card" :bal 0}
                                     {:name "loan" :bal 0}]}
                         {:name "income" :bal 0
                          :children [{:name "salary" :bal 0}
                                     {:name "interest" :bal 0}
                                     {:name "gifts" :bal 0}]}
                         {:name "expense" :bal 0
                          :children [{:name "groceries" :bal 0}
                                     {:name "vehicle" :bal 0
                                      :children [{:name "fuel" :bal 0}
                                                 {:name "repairs" :bal 0}]}
                                     {:name "shopping" :bal 0}
                                     {:name "clothes" :bal 0}
                                     {:name "jewellery" :bal 0}
                                     {:name "dining" :bal 0}
                                     {:name "furniture" :bal 0}
                                     {:name "appliances" :bal 0}
                                     {:name "entertainment" :bal 0
                                      :children [{:name "movies" :bal 0}]}
                                     {:name "utilities" :bal 0
                                      :children [{:name "electricity" :bal 0}
                                                 {:name "phone" :bal 0}
                                                 {:name "gas" :bal 0}
                                                 {:name "internet" :bal 0}]}
                                     {:name "household" :bal 0
                                      :children [{:name "maid" :bal 0}
                                                 {:name "laundry" :bal 0}
                                                 {:name "rent" :bal 0}
                                                 {:name "repairs" :bal 0}]}
                                     {:name "gifts" :bal 0}
                                     {:name "vacation" :bal 0
                                      :children [{:name "transportation" :bal 0}
                                                 {:name "accomodation" :bal 0}
                                                 {:name "dining" :bal 0}]}
                                     {:name "transportation" :bal 0}]}]))
