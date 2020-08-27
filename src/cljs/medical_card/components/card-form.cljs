(ns medical-card.card-form
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn on-change-field [field value]
  (rf/dispatch [:update-card field value]))

(defn on-submit-form [e]
  (.preventDefault e)
  (rf/dispatch [:save-card]))

(defn on-clear-form []
  (rf/dispatch [:new-card]))

(defn card-form-template []
  [:form {:class "card-form" :on-submit #(on-submit-form %)}
   [:label {:class "card-form__label"} "Full name: "
    [:input {:type      "text"
             :name      "full_name"
             :value     (:full_name @(rf/subscribe [:card]))
             :maxLength "100"
             :required  true
             :on-change #(on-change-field :full_name (-> % .-target .-value))}]]

   [:label {:class "card-form__label"} "Gender: "
    [:select {:name          "gender"
              :on-change     #(on-change-field :gender (-> % .-target .-value))
              :value         (or (:gender @(rf/subscribe [:card])) "")
              :required      true}
     [:option {:value "" :disabled true} ""]
     [:option {:value "Male"} "Male"]
     [:option {:value "Female"} "Female"]
     [:option {:value "Other"} "Other"]]]

   [:label {:class "card-form__label"} "Address: "
    [:input {:type "text"
             :name "address"
             :value (:address @(rf/subscribe [:card]))
             :maxLength "100"
             :required true
             :on-change #(on-change-field :address (-> % .-target .-value))}]]

   [:label {:class "card-form__label"} "Birthday: "
    [:input {:type "date"
             :name "birthday"
             :value (:birthday @(rf/subscribe [:card]))
             :min "1920-01-01"
             :required true
             :on-change #(on-change-field :birthday (-> % .-target .-value))}]]

   [:label {:class "card-form__label"} "Policy ID: "
    [:input {:type "number"
             :name "id_policy"
             :value (:id_policy @(rf/subscribe [:card]))
             :maxLength "11"
             :required true
             :on-change #(on-change-field :id_policy (-> % .-target .-value))}]]

   [:button {:class "card-form__submit app__button"
             :type  "submit"}
    (if (:id @(rf/subscribe [:card])) "Update card" "Save new card")]

   [:input {:class "card-form__clear app__button"
            :type  "button"
            :value "Clear"
            :on-click #(on-clear-form)
            :hidden (nil? @(rf/subscribe [:card]))}]])

(defn card-form [& [id]]
  (reagent/create-class
   {:display-name  "card-form"

    :component-did-mount #(if id (rf/dispatch [:load-card id]) nil)

    :reagent-render #(card-form-template)}))
