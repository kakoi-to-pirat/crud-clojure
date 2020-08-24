(ns client.route
  (:require [reitit.frontend :as reitit]))

(def routes
  (reitit/router
   [["/" :index]
    ["/about" :about]
    ["/card"
     ["/" :card-add]
     ["/:id" :card-edit]]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name routes route params))
    (:path (reitit/match-by-name routes route))))