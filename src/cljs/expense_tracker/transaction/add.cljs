(ns expense-tracker.transaction.add
  (:require [reagent.core :as r]
            [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]
            [expense-tracker.account.utils :as au]
            [expense-tracker.transaction.utils :as tu]
            [clojure.string :as str]
            [jayq.core :as jq]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; local states

(defn new-state []
  {:to 0 :from 0 :date ""
   :trans [(atom {:id 0 :type :to :val 0 :acc ""})
           (atom {:id 1 :type :from :val 0 :acc ""})]})
;; this state isn't local coz it can be modified from /core
(defonce app-state (r/atom (new-state)))
(defonce tmp (atom 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn new-trans [typeof] {:id (count (:trans @app-state)) :val 0 :type typeof})

(defn acc-validate [e t]
  (let [acc (u/trim-empty? app-state (.-value (.-target e)) :acc)]
    (if (or (= acc "") (empty? (filter #(= acc %) (au/filtered-accs->names @g/accounts))))
      (u/alert "Non-existent account!")
      (swap! t assoc :acc acc))))

(defn amt-equal [] (= (:to @app-state) (:from @app-state)))
(defn amt-of [e t] (reset! tmp (:val @t)))
(defn amt-ob [e t] (swap! app-state update-in [(:type @t)] #(+ (- % @tmp) (:val @t))))
(defn amt-validate [e t]
  (if-let [f (u/amt-validate? (.-value (.-target e)))]
    (swap! t assoc :val (if (= val "") 0 f))
    (u/alert "Only numbers and decimal allowed!")))

(defn update-accounts [trans] (tu/update-accounts trans + -))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-trans [t]
  (letfn [(rm []
            (swap! app-state update-in [:trans] #(remove (fn [x] (= (:id @t) (:id @x))) %))
            (swap! app-state update-in [(:type @t)] #(- % (:val @t))))]
    [:li [:input.form-control.acc {:type "text" :list "acc-names"
                                   :placeholder (:acc @t)
                                   :onBlur #(acc-validate % t)}]
     [:input.form-control.amt {:type "text" :placeholder (:val @t)
                               :onChange #(amt-validate % t)
                               :onFocus #(amt-of % t)
                               :onBlur #(amt-ob % t)}]
     [:p.del [:a {:href "#" :onClick rm}
          [:span.glyphicon.glyphicon-minus-sign {:aria-hidden "true"}]]]]))

(defn c-add []
  (let [tos (filter #(= :to (:type @%)) (:trans @app-state))
        froms (filter #(= :from (:type @%)) (:trans @app-state))]
    (letfn [(split [_ to-from] (swap! app-state
                                      update-in [:trans]
                                      conj (atom (new-trans to-from))))
            (datepicker [e] (.datepicker (jq/$ "#trans-date")))
            (snn [e] (let [date (let [d (u/trim-empty? app-state
                                                       (.-value (u/by-id "trans-date"))
                                                       :date)
                                      dt (u/jq->long d)]
                                  (if (= dt "Invalid date") d dt))
                           desc (u/trim-empty? app-state (.-value (u/by-id "trans-desc")) :desc)]
                       (cond (or (zero? (:to @app-state))
                                 (zero? (:from @app-state))
                                 (not (amt-equal)))
                             (u/alert "Amounts in both \"to\" and \"from\"
accounts should be the same,
and not-equal-to ZERO")
                             (= date "")
                             (u/alert "Date field cannot be empty!")
                             (let [accs (mapv #(:acc @%) (:trans @app-state))]
                               (not (= accs (into [] (into #{} accs)))))
                             (u/alert "The same account cannot appear
more than once in a transaction!")
                             :else
                             (do #_(println @g/transactions)
                                 (swap! g/transactions conj
                                        (conj @app-state {:id (count @g/transactions)
                                                          :date date
                                                          :desc desc}))
                                 #_(println @g/transactions)
                                 (update-accounts (:trans @app-state))
                                 (reset! app-state (new-state))))))
            (snd [e] (when (snn e) (reset! g/app-page {:page :home})))]
      [:div [:datalist {:id "acc-names"}
             (for [an (au/filtered-accs->names @g/accounts)]
               ^{:key (u/random)}[:option {:value an}])]
       [:div.row [:div.col-sm-12 [:label "Date"]
                  [:input.form-control {:id "trans-date"
                                        :type "text"
                                        :placeholder (:date @app-state)
                                        :onFocus datepicker}]]]
       [:div.row
        [:div.col-sm-6
         [:h2 "From Account(s)"
          [:small.pull-right [:a {:href "#" :onClick #(split % :from)}
                              [:span.glyphicon.glyphicon-plus-sign {:aria-hidden "true"}]]]]
         [:ul.list-unstyled.clearfix (for [x froms] ^{:key (u/random)} [c-trans x])]
         [:h3 "Total " [:span.pull-right (:from @app-state)]]]
        [:div.col-sm-6
         [:h2 "To Account(s)"
          [:small.pull-right [:a {:href "#" :onClick #(split % :to)}
                              [:span.glyphicon.glyphicon-plus-sign {:aria-hidden "true"}]]]]
         [:ul.list-unstyled.clearfix (for [x tos] ^{:key (u/random)} [c-trans x])]
         [:h3 "Total " [:span.pull-right (:to @app-state)]]]]
       [:div.row [:div.col-sm-12 [:label "Description"]
                  [:textarea.form-control {:id "trans-desc"}]]]
       [:div.row
        [:p]
        [:div.col-sm-6 [:button.btn.btn-default.pull-right {:onClick snd} "Save and Done"]]
        [:div.col-sm-6 [:button.btn.btn-default {:onClick snn} "Save and New"]]]])))
