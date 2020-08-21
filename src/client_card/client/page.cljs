(ns client.page
  (:require [client.route :as route]
            [client.cards-list :as cards-list]
            [reagent.session :as session]))

(def navigation [:nav {:class "card-section__navbar"}
                 [:a {:class "card-section__navbar-link app__button" :href (route/path-for :index)} "Home"]
                 [:a {:class "card-section__navbar-link app__button" :href (route/path-for :card-add)} "Add cart"]
                 [:a {:class "card-section__navbar-link app__button" :href (route/path-for :about)} "About"]])

(defn template [content]
  [:section {:class "card-section"}
   [:h1 {:class "app__header"} "Client cards manager"]
   navigation
   content])

(defn home-page [] [cards-list/cards-list])

(defn about-page []
  [:div
   [:h1 "About app"]
   [:p "This is demo app of client cards"]])

(defn card-edit-page []
  (let [card-id (get-in (session/get :route) [:route-params :id])]
    [:div
     [:h1 "card-edit"]
     [:p "card-edit " card-id]]))

(defn card-add-page []
  [:div
   [:h1 "card-add"]
   [:p "card-add"]])