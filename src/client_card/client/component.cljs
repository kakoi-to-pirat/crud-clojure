(ns client.component
  (:require [ajax.core :as ajax]
            [reagent.core :as reagent]))

(defn root []
  (let [loaded-cards (reagent/atom nil)]
    (ajax/GET "/api/card/"
      {:handler (fn [data]
                  (reset! loaded-cards data))})
    (fn []
      [:div "Loaded cards in the background:" (str @loaded-cards)])))

