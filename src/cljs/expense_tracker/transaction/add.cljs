(ns expense-tracker.transaction.add
  (:require [reagent.core :as r]
            [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]
            [expense-tracker.accounts :as a]
            [clojure.string :as str]
            [jayq.core :as jq]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; local states

(defn new-state []
  {:to 0 :from 0 :date ""
   :trans [(atom {:id 0 :type :to :val 0 :acc ""})
           (atom {:id 1 :type :from :val 0 :acc ""})]})
(defonce app-state (r/atom (new-state)))
(defonce tmp (atom 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn new-trans [typeof] {:id (count (:trans @app-state)) :val 0 :type typeof})

(defn acc-validate [e t]
  (let [acc (str/trim (.-value (.-target e)))]
    (if (and (= (:acc @t) "")
             (or (= acc "") (empty? (filter #(= acc %) (a/accs->names @g/accounts)))))
      (u/alert "Non-existent account!")
      (swap! t assoc :acc (if-not (= acc "") acc (:acc @t))))))

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

(defn find-index [haystack needle key]
  (loop [hs haystack, i 0]
    (if (= (key (first hs)) needle)
      i
      (recur (rest hs) (inc i)))))

(defn update-accounts [trans]
  (mapv (fn [x]
          (let [t @x]
            (loop [accs (str/split (:acc t) ":")
                   update-path []
                   root @g/accounts]
              (when-not (empty? accs)
                (let [nm (first accs)
                      idx (find-index root nm :name)]
                  (swap! g/accounts update-in (conj update-path idx :bal)
                         #(if (= (:type t) :to) (+ % (:val t)) (- % (:val t))))
                  (recur (rest accs)
                         (conj update-path idx :children)
                         (:children (nth root idx))))))))
        trans))

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

(defn c-add-transaction []
  (let [tos (filter #(= :to (:type @%)) (:trans @app-state))
        froms (filter #(= :from (:type @%)) (:trans @app-state))]
    (letfn [(split [_ to-from] (swap! app-state
                                      update-in [:trans]
                                      conj (atom (new-trans to-from))))
            (datepicker [] (.datepicker (jq/$ "#trans-date")))
            (snn [] (let [date (.-value (u/by-id "trans-date"))]
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
                                       (conj @app-state {:date (u/jq->long date)}))
                                #_(println @g/transactions)
                                (update-accounts (:trans @app-state))
                                (reset! app-state (new-state))))))
            (snd [] (when (snn) (reset! g/app-page {:page :home})))]
      [:div [:datalist {:id "acc-names"}
             (for [an (a/accs->names @g/accounts)]
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
       [:div.row
        [:div.col-sm-6 [:button.btn.btn-default.pull-right {:onClick snd} "Save and Done"]]
        [:div.col-sm-6 [:button.btn.btn-default {:onClick snn} "Save and New"]]]])))
