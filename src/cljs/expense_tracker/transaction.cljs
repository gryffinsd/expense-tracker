(ns expense-tracker.transaction
  (:require [reagent.core :as r]
            [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]
            [clojure.string :as str]))

(declare new-state)
(defonce app-state (r/atom (new-state)))
(defonce tmp (atom 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn new-state []
  {:to 0 :from 0
   :trans [(atom {:id 0 :type :to :val 0 :acc ""})
           (atom {:id 1 :type :from :val 0 :acc ""})]})

(defn new-trans [typeof] {:id (count @app-state) :val 0 :type typeof})

(defn acc-validate [e t]
  (let [acc (str/trim (.-value (.-target e)))]
    (if (or (= acc "") (empty? (filter #(= acc %) @g/account-names)))
      (u/alert "Non-existent account!")
      (swap! t assoc :acc acc))))

(defn amt-equal [] (= (:to @app-state) (:from @app-state)))
(defn amt-of [e t] (reset! tmp (:val @t)))
(defn amt-ob [e t] (swap! app-state update-in [(:type @t)] #(+ (- % @tmp) (:val @t))))
(defn amt-validate [e t]
  (let [val (.-value (.-target e))
        f (js/parseFloat val)
        i (js/parseInt val)]
    (when-not (or (= val "") ; empty string
                  (= i f) ; ending w/ decimal point
                  (= (str f) val))
      (u/alert "Only numbers and decimal allowed!"))
    (swap! t assoc :val (if (= val "") 0 f))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-trans [t]
  (letfn [(rm [] (swap! app-state #(remove (fn [x] (= (:id @t) (:id @x))) %)))]
    [:li [:input.form-control.acc {:type "text" :list "acc-names"
                                   :placeholder (:acc @t)
                                   :onBlur #(acc-validate % t)}]
     [:input.form-control.amt {:type "text" :placeholder (:val @t)
                               :onChange #(amt-validate % t)
                               :onFocus #(amt-of % t)
                               :onBlur #(amt-ob % t)}]
     [:span [:a {:href "#" :onClick rm} "Rm"]]]))

(defn c-add-transaction []
  (let [tos (filter #(= :to (:type @%)) (:trans @app-state))
        froms (filter #(= :from (:type @%)) (:trans @app-state))]
    (letfn [(split [_ to-from]
              (swap! app-state
                     update-in [:trans]
                     conj (atom (new-trans to-from))))
            (snn [] (if (or (zero? (:to @app-state))
                            (zero? (:from @app-state))
                            (not (amt-equal)))
                      (u/alert "Amounts in both \"to\" and \"from\"
accounts should be the same,
and not-equal-to ZERO")
                      (do (swap! g/transactions conj @app-state)
                          (reset! app-state (new-state)))))
            (snd [] nil)]
      [:div [:datalist {:id "acc-names"}
             (for [an @g/account-names]
               ^{:key (u/random)}[:option {:value an}])]
       [:div.row
             [:div.col-sm-6
              [:h2 "To Account(s)"
               [:small.pull-right [:a {:href "#" :onClick #(split % :to)} "Split"]]]
              [:ul.list-unstyled.clearfix (for [x tos] ^{:key (u/random)} [c-trans x])]
              [:h3 "Total " [:span.pull-right (:to @app-state)]]]
             [:div.col-sm-6
              [:h2 "From Account(s)"
               [:small.pull-right [:a {:href "#" :onClick #(split % :from)} "Split"]]]
              [:ul.list-unstyled.clearfix (for [x froms] ^{:key (u/random)} [c-trans x])]
              [:h3 "Total " [:span.pull-right (:from @app-state)]]]]
       [:div.row
        [:div.col-sm-6 [:button.btn.btn-default.pull-right {:onClick snd} "Save and Done"]]
        [:div.col-sm-6 [:button.btn.btn-default {:onClick snn} "Save and New"]]]])))
