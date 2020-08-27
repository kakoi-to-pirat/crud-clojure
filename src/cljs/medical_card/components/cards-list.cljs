(ns medical-card.cards-list
  (:require [reagent.core :as reagent]
            [medical-card.route :as route]
            [re-frame.core :as rf]
            [medical-card.store :refer []]))

(defn on-delete-card [id]
  (rf/dispatch [:delete-card id]))

(defn cards-list-template []
  (if @(rf/subscribe [:loading?])
    [:div "Please wait while cards is loading..."]
    [:table {:key 2003 :class "cards-list" :width "100%"}

     [:thead {:key 200 :class "cards-list__header"}
      [:tr
       [:th "Policy ID"]
       [:th "Full name"]
       [:th "Gender"]
       [:th "Address"]
       [:th "Birthday"]]]

     [:tbody
      (map-indexed
       (fn [index card]
         (let [{:keys [id full_name gender address birthday id_policy]} card]
           [:tr {:key index
                 :class "cards-list__item"}
            [:th id_policy]
            [:th full_name]
            [:th gender]
            [:th address]
            [:th birthday]
            [:th {:width 60} [:a {:class "cards-list__item-button app__button"
                                  :href (route/path-for :card-edit {:id id})}
                              "Edit"]]
            [:th {:width 60} [:button {:class "cards-list__item-button app__button"
                                       :on-click #(on-delete-card id)}
                              "Delete"]]]))
       @(rf/subscribe [:cards]))]]))

(defn cards-list []
  (reagent/create-class
   {:display-name  "cards-list"

    :component-did-mount #(rf/dispatch [:load-cards])

    :reagent-render #(cards-list-template)}))
