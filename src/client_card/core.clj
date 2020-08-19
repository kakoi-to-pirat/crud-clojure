(ns client-card.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.util.response :refer [response status]]
            [dotenv :refer [env]]
            [environ.core :as environ]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [compojure.route :refer [not-found resources]]
            [client-card.db  :as db]))

(defn response-error [error]
  (status (response {:error (.getMessage error)}) 500))


;; -------------------------
;; HANDLERS


(defn index-handler [request]
  (try (response "Hello!")
       (catch Exception e (response-error e))))

(defn cards-view-handler [request]
  (try (response (db/get-all-cards))
       (catch Exception e (response-error e))))

(defn card-view-handler [id]
  (try  (response (db/get-card (Integer. id)))
        (catch Exception e (response-error e))))

(defn card-update-handler [request]
  (let [{:keys [params]} request
        {:keys [id]} params]
    (try
      (db/update-card (Integer. id) params)
      (response {:updated-card params})
      (catch Exception e (response-error e)))))

(defn card-save-handler [request]
  (let [{:keys [params]} request]
    (try
      (db/create-card params)
      (response {:updated-card params})
      (catch Exception e (response-error e)))))

(defn card-delete-handler [id]
  (try
    (db/delete-card (Integer. id))
    (response {:deletes-card-id id})
    (catch Exception e (response-error e))))

(defn not-found-handler [request]
  (response {:message "Sorry, the page you requested was not found!"}))


;; -------------------------
;; ROUTES


(defroutes app
  (GET "/" [] index-handler)

  (GET "/card/" [] (-> cards-view-handler
                       wrap-json-response))

  (GET "/card/:id" [id] ((-> card-view-handler
                             wrap-json-response) id))

  (POST "/card/" [] (-> card-save-handler
                        wrap-keyword-params
                        wrap-json-params
                        wrap-json-response))

  (PUT "/card/:id" [] (-> card-update-handler
                          wrap-keyword-params
                          wrap-json-params
                          wrap-json-response))

  (DELETE "/card/:id" [id] ((-> card-delete-handler
                                wrap-json-response) id))

  (not-found (-> not-found-handler
                 wrap-json-response))

  (resources "/"))


;; -------------------------
;; SERVER


(defonce server (atom nil))

(defn start-server [& [port]]
  (let [port (Integer. (or (:port port) (env "APP_PORT") 3000))]
    (if (= (environ/env :environment) "development")
      (reset! server (webserver/run-jetty (wrap-reload #'app)
                                          {:port port :join? false}))
      (webserver/run-jetty app {:port port}))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(defn -main []
  (db/migrate-up)
  (start-server))


;; -------------------------
;; REPL


(comment
  (start-server)
  (stop-server))