(ns client.routes
  (:require [reitit.frontend :as reitit]))

(def routes
  (reitit/router
   [["/" :index]
    ["/about" :about]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name routes route params))
    (:path (reitit/match-by-name routes route))))