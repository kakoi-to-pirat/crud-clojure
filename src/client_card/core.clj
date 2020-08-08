(ns client-card.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [dotenv :refer [env]]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]))

(defn get-card-by-id [id] {:id id :name "Jhon Paterson"})

(defn create-card [name] {:id 1 :name name})


;; VIEWS


(defn index-view [request]
  {:status 200
   :body "<h1>Hello, Clojure World</h1><p>Welcome to your first Clojure app.</p>"
   :headers {}})

(defn card-view [id]
  (let [card (get-card-by-id id)
        {:keys [name]} card]
    {:status 200
     :body (format "View card is %s %s" id name)}))

(defn card-update [request]
  (let [{:keys [id name email]} (:params request)]
    {:status 200
     :body (format "Card whis id: %s set name: %s and email: %s" id name email)}))

(defn card-delete [id]
  {:status 200
   :body (format "Card whis id: %s was deleted" id)})

(defn card-create [request]
  (let [{:keys [name email]} (:params request)]
    {:status 200
     :body (format "Card created with name: %s and email: %s" name email)}))

(def not-found-view "<h1>This is not the page you are looking for</h1> <p>Sorry, the page you requested was not found!</p>")


;; ROUTES


(defroutes app
  (GET "/" [] index-view)
  (POST "/card" []  (wrap-params (wrap-keyword-params card-create)))
  (GET "/card/:id" [id] (card-view id))
  (PUT "/card/:id" [] (wrap-params (wrap-keyword-params card-update)))
  (DELETE "/card/:id" [] card-delete)
  (not-found not-found-view))


;; SERVER


(defn -main
  [& [port]]
  (let [port (Integer. (or port (env "PORT") 3000))]
    (webserver/run-jetty
     app
     {:port  (Integer. port)})))

(defn -dev-main
  [& [port]]
  (let [port (Integer. (or port (env "PORT") 3000))]
    (webserver/run-jetty
     (wrap-reload #'app)
     {:port (Integer. port)
      :join? false})))

(env :PORT)
