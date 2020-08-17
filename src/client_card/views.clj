(ns client-card.views
  (:require [hiccup.page :refer [include-css html5]]))

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "/app.css")])

(defn template [content]
  (html5
   (head)
   [:body
    [:main {:class "app"}
     [:h1 {:class "app__header"} "Client cards manager"]
     [:section {:class "card-section"}
      [:nav {:class "card-section__navbar"}
       [:a {:class "card-section__navbar-link app__button" :href "/"} "Main"]
       [:a {:class "card-section__navbar-link app__button" :href "/card/add/"} "Add"]]
      content]]]))

(defn cards-list [cards]
  [:table {:class "cards-list" :width "100%"}
   [:tr {:class "cards-list__header"} [:th "Policy ID"] [:th "Full name"] [:th "Gender"] [:th "Address"] [:th "Birthday"]]
   (map (fn [card]
          (let [{:keys [id full_name gender address birthday id_policy]} card]
            [:tr
             {:class "cards-list__item"}
             [:th id_policy]
             [:th full_name]
             [:th gender]
             [:th address]
             [:th birthday]
             [:th {:width 60} [:a {:class "cards-list__item-button app__button" :href (format "/card/edit/%s" id)} "Edit"]]
             [:th {:width 60} [:form {:class "cards-list__item-form" :action (format "/card/delete/%s" id) :method "POST"}
                               [:button {:class "cards-list__item-button app__button" :type "submit"} "Delete"]]]]))
        cards)])

(defn index-view [cards]
  (template (cards-list cards)))

(defn card-view [card]
  (template (cards-list [card])))

(defn card-form-view [& [card]]
  (let [{:keys [id full_name gender address birthday id_policy]} card]
    (template
     [:form {:class "card-form" :action (if id (format "/card/update/%s" id) "/card/save/") :method "POST"}
      [:label {:class "card-form__label"} "Full name: "
       [:input {:type "text" :name "full_name" :value full_name :maxlength "100" :required true}]]
      [:label {:class "card-form__label"} "Gender: "
       [:select {:name "gender"}
        [:option {:value "Male" :selected (= gender "Male")} "Male"]
        [:option {:value "Female" :selected (= gender "Female")} "Female"]
        [:option {:value "Other" :selected (= gender "Other")} "Other"]]]
      [:label {:class "card-form__label"} "Address: "
       [:input {:type "text" :name "address" :value address :maxlength "100" :required true}]]
      [:label {:class "card-form__label"} "Birthday: "
       [:input {:type "date" :name "birthday" :value birthday :min "1920-01-01" :required true}]]
      [:label {:class "card-form__label"} "Policy ID: "
       [:input {:type "number" :name "id_policy" :value id_policy :maxlength "11" :required true}]]
      [:button {:class "card-form__submit app__button" :type "submit"} (if id "Update card" "Add new card")]])))

(defn not-found-view []
  (template [:div {:class "not-found-page"}
             [:h2 "There is no page you are looking for"]
             [:p "Sorry, the page you requested was not found!"]]))

(defn error-view [error]
  (template [:div {:class "error-page"}
             [:h2 "Was error!"]
             [:h3 "Sorry, an error occurred on the server side:"]
             [:p (format "%s" error)]]))