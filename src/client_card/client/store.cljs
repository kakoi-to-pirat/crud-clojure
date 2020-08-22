(ns client.store
  (:require [ajax.core :as ajax]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))


;; -------------------------
;; STATE


(defonce cards (reagent/atom nil))

(defonce loading? (reagent/atom false))


;; -------------------------
;; API


(defn load-cards [] (ajax/GET "/api/card/"
                      {:response-format :json
                       :keywords? true
                       :handler (fn [res] (reset! cards res))}))


;; -------------------------
;; EVENTS


(rf/reg-event-db
 :load-cards
 (fn [db _]
   (load-cards)
   (assoc-in db [:cards] @cards)))

(rf/reg-sub
 :cards
 (fn [db _]
   (get-in db [:cards] [])))

(rf/reg-event-db
 :set-loading
 (fn [db [_ value]]
   (assoc-in db [:loading?] value)))

(rf/reg-sub
 :loading?
 (fn [db _]
   (get-in db [:loading?] false)))