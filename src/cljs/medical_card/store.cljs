(ns medical-card.store
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]))


;;-------------------------
;; Api


(rf/reg-event-fx
 :load-cards
 (fn [{:keys [db]} _]
   (assoc db :loading?  true)
   {:http-xhrio {:method          :get
                 :uri             "/api/card/"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:on-success-load-cards]
                 :on-failure      [:on-failure]}}))

(rf/reg-event-fx
 :load-card
 (fn [{:keys [db]} [_ id]]
   (assoc db :loading?  true)
   {:http-xhrio {:method          :get
                 :uri             (str "/api/card/" id)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:on-success-load-card]
                 :on-failure      [:on-failure]}}))

(rf/reg-event-fx
 :delete-card
 (fn [{:keys [db]} [_ id]]
   (assoc db :loading?  true)
   {:http-xhrio {:method          :delete
                 :uri             (str "/api/card/" id)
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:on-success-delete-card]
                 :on-failure      [:on-failure]}}))

(rf/reg-event-fx
 :save-card
 (fn [{:keys [db]} _]
   (assoc db :loading?  true)
   (let [{:keys [card]} db
         {:keys [id]} card]
     {:http-xhrio {:method          (if id :put :post)
                   :uri             (if id (str "/api/card/" id) "/api/card/")
                   :params          card
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:on-success-save-card]
                   :on-failure      [:on-failure]}})))


;;-------------------------
;; Error handlers


(rf/reg-event-db
 :on-failure
 (fn [db [_ error-res]]
   (-> db
       (assoc :loading?  false)
       (assoc :error (str error-res)))))

(rf/reg-sub
 :error
 (fn [db _]
   (get-in db [:error] nil)))


;;-------------------------
;; All cards


(rf/reg-event-db
 :on-success-load-cards
 (fn [db [_ res]]
   (-> db
       (assoc :loading?  false)
       (assoc :cards res))))

(rf/reg-sub
 :cards
 (fn [db _]
   (get-in db [:cards] [])))


;;-------------------------
;; Is loading http request?


(rf/reg-sub
 :loading?
 (fn [db _]
   (get-in db [:loading?] true)))


;;-------------------------
;; Current card


(rf/reg-event-db
 :on-success-save-card
 (fn [db [_ _res]]
   (-> db
       (assoc :loading?  false)
       (assoc :card (:card nil)))))

(rf/reg-event-db
 :on-success-load-card
 (fn [db [_ res]]
   (-> db
       (assoc :loading?  false)
       (assoc :card res))))

(rf/reg-event-db
 :on-success-delete-card
 (fn [db [_ res]]
   (-> db
       (assoc :loading?  false)
       (assoc :cards
              (filter #(not= (:id %)
                             (js/parseInt (:deleted-card-id res)))
                      (:cards db))))))

(rf/reg-event-db
 :update-card
 (fn [db [_ field value]]
   (assoc-in db [:card field] value)))

(rf/reg-event-db
 :new-card
 (fn [db [_]]
   (assoc db :card nil)))

(rf/reg-sub
 :card
 (fn [db _]
   (get-in db [:card] nil)))
