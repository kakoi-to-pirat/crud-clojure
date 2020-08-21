(ns client.store
  (:require [ajax.core :as ajax]
            [reagent.core :as reagent]))

(defonce cards (reagent/atom nil))

(defn load-cards [] (ajax/GET "/api/card/"
                      {:response-format :json
                       :keywords? true
                       :handler (fn [res] (reset! cards res))}))
