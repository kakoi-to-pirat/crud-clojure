(ns client.router
  (:require [reagent.session :as session]
            [reitit.frontend :as reitit]
            [reagent.core :as reagent]
            [clerk.core :as clerk]
            [accountant.core :as accountant]
            [client.page :as page]
            [client.route :as routes]))


;; -------------------------
;; Translate routes -> page components


(defn get-page-for [route]
  (case route
    :index #'page/home-page
    :about #'page/about-page))


;; -------------------------
;; Page mounting component


(defn get-current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:main page/navigation [page]])))


;; -------------------------
;; Initialize router


(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path routes/routes path)
            route (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (get-page-for route)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path routes/routes path)))})
  (accountant/dispatch-current!))