(ns medical-card.router
  (:require [reagent.session :as session]
            [reitit.frontend :as reitit]
            [reagent.core :as reagent]
            [clerk.core :as clerk]
            [accountant.core :as accountant]
            [medical-card.page :as page]
            [medical-card.route :as routes]))


;; -------------------------
;; Translate routes -> page components


(defn get-page-for [route]
  (case route
    :index #'page/home-page
    :about #'page/about-page
    :card-edit #'page/card-edit-page
    :card-add #'page/card-add-page))


;; -------------------------
;; Page mounting component


(defn get-current-page []
  (let [page (:current-page (session/get :route))] [page/template [page]]))


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