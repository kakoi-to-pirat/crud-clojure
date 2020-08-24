(ns medical-card.core
  (:require [medical-card.db  :as db]
            [medical-card.view :as view]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [compojure.route :refer [not-found resources]]
            [dotenv :refer [env]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.response :refer [response status]]
            [ring.adapter.jetty :as webserver]))

(defn response-error [error]
  (status (response {:error (.getMessage error)}) 500))


;; -------------------------
;; HANDLERS


(defn index-handler [_request]
  (try {:status 200
        :headers {"Content-Type" "text/html"}
        :body (view/index)}
       (catch Exception e (response-error e))))

(defn cards-view-handler [_request]
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


;; -------------------------
;; ROUTES


(defroutes app
  (GET "/" [] index-handler)

  (GET "/api/card/" [] (-> cards-view-handler
                           wrap-json-response))

  (GET "/api/card/:id" [id] ((-> card-view-handler
                                 wrap-json-response) id))

  (POST "/api/card/" [] (-> card-save-handler
                            wrap-keyword-params
                            wrap-json-params
                            wrap-json-response))

  (PUT "/api/card/:id" [] (-> card-update-handler
                              wrap-keyword-params
                              wrap-json-params
                              wrap-json-response))

  (DELETE "/api/card/:id" [id] ((-> card-delete-handler
                                    wrap-json-response) id))

  (resources "/")

  (not-found index-handler))


;; -------------------------
;; SERVER


(defn start-server [& [port]]
  (let [port (Integer. (or port (env "PORT") 3000))]
    (webserver/run-jetty app {:port port})))

(defn -main [& args]
  (db/migrate-up)
  (start-server args))