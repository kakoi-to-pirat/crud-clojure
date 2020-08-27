(ns medical-card.message
  (:require [re-frame.core :as rf]
            [medical-card.store :refer []]))

(defn success [& [msg]]
  [:section {:class "msg-box success-box"}
   [:h3 {:class "msg-box__header"} "Success!"]
   [:p {:class "msg-box__msg"} msg]])

(defn failure [& [msg]]
  [:section {:class "msg-box failure-box"}
   [:h3 {:class "msg-box__header"} "Failure!"]
   [:p {:class "msg-box__msg"} msg]])

(defn message []
  (let [{:keys [type content]} @(rf/subscribe [:message])]
    (js/setTimeout #(rf/dispatch [:hide-message]) 3000)
    (case type
      "success" (success content)
      "failure" (failure content)
      nil)))