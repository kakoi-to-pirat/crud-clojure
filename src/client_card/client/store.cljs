(ns client.store
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]))

(rf/reg-event-fx
 :load-cards
 (fn [{:keys [db]} _]
   {:db   (assoc db :show-twirly true)
    :http-xhrio {:method          :get
                 :uri             "/api/card/"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:on-success-load-cards]
                 :on-failure      [:on-failure-load-cards]}}))

(rf/reg-event-db
 :on-success-load-cards
 (fn [db [_ res]]
   (-> db
       (assoc :loading?  false)
       (assoc :cards res))))

(rf/reg-event-db
 :on-failure-load-cards
 (fn [db [_ _res]]
   (assoc db :loading?  false)))

(rf/reg-sub
 :cards
 (fn [db _]
   (get-in db [:cards] [])))

(rf/reg-sub
 :loading?
 (fn [db _]
   (get-in db [:loading?] true)))