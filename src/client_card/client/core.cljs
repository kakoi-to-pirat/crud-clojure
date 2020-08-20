(ns client.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]))

(defn page [] [:div "Hello!"])

(defn log [& args] (apply (.-log js/console) args))

(def app-container (.getElementById js/document "app"))

(defn mount-root []
  (rdom/render page app-container))