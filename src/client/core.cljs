(ns client.core
  (:require [reagent.dom :as rdom]
            [client.router :as router]))

(defn mount-root []
  (rdom/render [router/get-current-page] (.getElementById js/document "app")))

(defn init! []
  (router/init!)
  (mount-root))