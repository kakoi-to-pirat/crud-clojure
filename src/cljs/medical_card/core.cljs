(ns medical-card.core
  (:require [reagent.dom :as rdom]
            [medical-card.router :as router]))

(defn mount-root []
  (rdom/render [router/get-current-page] (.getElementById js/document "app")))

(defn init! []
  (router/init!)
  (mount-root))