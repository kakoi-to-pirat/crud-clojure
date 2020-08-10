(ns client-card.views
  (:require [hiccup.page :refer [include-css html5]]))

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
  ;;  (include-css "/css/site.css")
   ])

(defn template [content]
  (html5
   (head)
   [:body
    [:main {:class "app"}
     [:h1 "Client cards manager"]
     [:section {:class "card-section"}
      [:nav {:class "navbar"}
       [:a {:href "/"} "All cards"]
       [:a {:href "/card/add/"} "Add card"]]
      content]]]))

(defn cards-list [cards]
  [:table {:class "cards-list" :border 1 :width "100%"}
   [:tr [:th "Policy ID"] [:th "Full name"] [:th "Gender"] [:th "Address"] [:th "Birthday"]]
   (map (fn [card]
          (let [{:keys [id full_name gender address birthday id_policy]} card]
            [:tr
             {:class "cards-list__item"}
             [:th id_policy]
             [:th full_name]
             [:th gender]
             [:th address]
             [:th birthday]
             [:th [:a {:href (format "/card/edit/%s" id)} "Edit"]]
             [:th [:form {:action (format "/card/delete/%s" id) :method "POST"}
                   [:button {:type "submit"} "Delete"]]]]))
        cards)])

(defn index-view [cards]
  (template [:div [:h2 "Cards list"] (cards-list cards)]))

(defn card-view [card]
  (template [:div [:h2 "Card view"] (cards-list [card])]))

(defn card-form-view [& [card]]
  (let [{:keys [id full_name gender address birthday id_policy]} card]
    (template
     [:form {:class "card-form" :action (if id (format "/card/update/%s" id) "/card/save/") :method "POST"}
      [:h2 (if id "Editing card" "Creating new card")]
      [:label {:class "card-form__label"} "Full name: "
       [:input {:type "text" :name "full_name" :value full_name :maxlength "100"}]]
      [:label {:class "card-form__label"} "Gender: "
       [:select {:name "gender"}
        [:option {:value "Male" :selected (= gender "Male")} "Male"]
        [:option {:value "Female" :selected (= gender "Female")} "Female"]
        [:option {:value "Other" :selected (= gender "Other")} "Other"]]]
      [:label {:class "card-form__label"} "Address: "
       [:input {:type "text" :name "address" :value address :maxlength "100"}]]
      [:label {:class "card-form__label"} "Birthday: "
       [:input {:type "date" :name "birthday" :value birthday}]]
      [:label {:class "card-form__label"} "Policy ID: "
       [:input {:type "number" :name "id_policy" :value id_policy :maxlength "11"}]]
      [:button {:class "card-form__submit" :type "submit"} "Save"]])))

(defn not-found-view []
  (template [:div {:class "not-found-page"}
             [:h2 "This is not the page you are looking for"]
             [:p "Sorry, the page you requested was not found!"]]))

(defn error-view [error]
  (template [:div {:class "error-page"}
             [:h2 "Was error!"]
             [:h3 "Sorry, an error occurred on the server side:"]
             [:p (format "%s" error)]]))