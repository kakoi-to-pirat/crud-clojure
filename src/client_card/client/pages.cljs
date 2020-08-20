(ns client.pages
  (:require [client.routes :as routes]))

(def navigation [:nav
                 [:a {:href (routes/path-for :index)} "Home"] " | "
                 [:a {:href (routes/path-for :about)} "About"]])

(defn home-page []
  (fn []
    [:div
     [:h1 "Welcome to demo app"]
     [:ul
      [:li [:a {:href (routes/path-for :about)} "About"]]]]))

(defn about-page []
  (fn [] [:div
          [:h1 "About todo"]
          [:p "This is demo app with todo list"]]))