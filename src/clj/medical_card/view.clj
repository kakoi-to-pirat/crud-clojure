(ns medical-card.view
  (:require [hiccup.page :refer [include-js include-css html5]]
            [environ.core :as environ]))

(def css-url (if (= (environ/env :environment) "development")
               "/css/app.css"
               "/css/app.min.css"))

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css css-url)])

(defn index []
  (html5
   (head)
   [:body
    [:main#app {:class "app"}
     [:h1 "Welcome to clients card!"]
     [:p "Please wait while app is waking up ..."]]
    (include-js "/js/app.js")]))

(defn not-found []
  (html5
   (head)
   [:body [:main {:class "not-found-page"}
           [:h1 "There is no page you are looking for"]
           [:p "Sorry, the page you requested was not found!"]]]))