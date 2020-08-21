(ns client.page
  (:require [client.route :as route]
            [client.component :as component]))

(def navigation [:nav
                 [:a {:href (route/path-for :index)} "Home"] " | "
                 [:a {:href (route/path-for :about)} "About"]])

(defn home-page []
  (fn []
    [:div
     [:h1 "Welcome to demo app"]
     [:ul
      [:li [:a {:href (route/path-for :about)} "About"]]]
     [component/root]]))

(defn about-page []
  (fn [] [:div
          [:h1 "About todo"]
          [:p "This is demo app with todo list"]]))