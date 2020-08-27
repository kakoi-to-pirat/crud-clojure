(ns medical-card.page
  (:require [medical-card.route :as route]
            [medical-card.cards-list :as cards-list]
            [medical-card.card-form :as card-form]
            [medical-card.message :as message]
            [reagent.session :as session]))

(def navigation [:nav {:class "card-section__navbar"}
                 [:a {:class "card-section__navbar-link app__button" :href (route/path-for :index)} "Home"]
                 [:a {:class "card-section__navbar-link app__button" :href (route/path-for :card-add)} "Cart"]
                 [:a {:class "card-section__navbar-link app__button" :href (route/path-for :about)} "About"]])

(defn template [content]
  [:section {:class "card-section"}
   [:h1 {:class "app__header"} "Client cards manager"]
   navigation
   content
   (message/message)])

(defn home-page [] [cards-list/cards-list])

(defn about-page []
  [:div
   [:h1 "About app"]
   [:p "This is demo app of client cards"]])

(defn card-edit-page []
  (let [card-id (get-in (session/get :route) [:route-params :id])]
    (card-form/card-form card-id)))

(defn card-add-page []
  (card-form/card-form))