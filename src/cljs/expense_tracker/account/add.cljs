(ns expense-tracker.account.add
  (:require [reagent.core :as r]
            [expense-tracker.utils :as u]
            [expense-tracker.globals :as g]
            [expense-tracker.account.utils :as au]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; local states

(defn new-state []
  {:name "" :init-bal 0 :bal 0})
;; this state isn't local coz it can be modified from /core
(defonce app-state (r/atom (new-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn name-ob [e]
  (let [val (u/trim-empty? app-state (.-value (.-target e)) :name)
        accs (au/accs->names @g/accounts)]
    (cond (= val "")
          (u/alert "Account name cannot be empty!")

          (u/contains (str/join " " accs) (str (.-value (u/by-id "parent")) ":" val))
          (u/alert "An account with the same name (and parent) already exists!")

          :else
          (swap! app-state assoc :name val))))

(defn amt-ob [e t]
  (if-let [f (u/amt-validate? (.-value (.-target e)))]
    (let [amt (if (= val "") 0 f)]
      (swap! app-state assoc
             :init-bal amt
             :bal amt))
    (u/alert "Only numbers and decimal allowed!")))

(defn acc-add [acc]
  (swap! g/accounts
         update-in (au/accs->indices (str/split (:parent acc) #":"))
         conj (dissoc acc :parent)))

(defn snn [e]
  (let [nm (str/lower-case (.-value (u/by-id "name")))
        parent (.-value (u/by-id "parent"))
        bal (let [b (.-value (u/by-id "init-bal"))] (if (empty? b) 0 b))]
    (if (= nm "")
      (u/alert "Account name cannot be empty!")
      (do (acc-add {:name nm :parent parent :init-bal bal :bal bal})
          (reset! app-state (new-state))))))

(defn snd [e] (when (snn e) (reset! g/app-page {:page :home})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components and views

(defn c-add-account []
  [:div [:div.form-group [:label "Account Name"]
         [:input.form-control {:type "text"
                               :id "name"
                               :onBlur name-ob}]]
   [:div.form-group [:label "Parent Account"]
    [:select.form-control {:id "parent"}
     (for [an (au/accs->names @g/accounts)]
       ^{:key (u/random)}[:option {:value an} an])]]
   [:div.form-group [:label "Initial Balance"]
    [:input.form-control {:type "text" :id "init-bal" :placeholder 0
                          :onBlur amt-ob}]]
   [:div.row
    [:p]
    [:div.col-sm-6 [:button.btn.btn-default.pull-right {:onClick snd} "Save and Done"]]
    [:div.col-sm-6 [:button.btn.btn-default {:onClick snn} "Save and New"]]]])
