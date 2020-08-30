(ns medical-card.core
  (:require [medical-card.db  :as db]
            [medical-card.view :as view]
            [compojure.core :refer [defroutes context GET POST PUT DELETE]]
            [compojure.route :refer [not-found resources]]
            [dotenv :refer [env]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.response :refer [response status]]
            [ring.adapter.jetty :as webserver]))

(defn response-error [error]
  (status (response {:error (.getMessage error)}) 500))

(defn validate-card [data]
  (let [{:keys [id_policy]} data]
    (if (first (db/get-card-by-policy-id (Integer. id_policy)))
      (throw (Exception. "A client with such a policy already exists"))
      true)))

;; -------------------------
;; HANDLERS


(defn index-handler [_request]
  (try (status (response (view/index)) 200)
       (catch Exception e (response-error e))))

(defn cards-view-handler [_request]
  (try (response (db/get-all-cards))
       (catch Exception e (response-error e))))

(defn card-view-handler [id]
  (try  (->> (db/get-card (Integer. id))
             first
             response)
        (catch Exception e (response-error e))))

(defn card-update-handler [request]
  (let [{:keys [params]} request
        {:keys [id]} params]
    (try
      (db/update-card (Integer. id) params)
      (response {:card params})
      (catch Exception e (response-error e)))))

(defn card-save-handler [request]
  (let [{:keys [params]} request
        {:keys [id_policy]} params]
    (try
      (validate-card params)
      (db/create-card params)
      (response {:card (-> (Integer. id_policy)
                           db/get-card-by-policy-id
                           first)})
      (catch Exception e (response-error e)))))

(defn card-delete-handler [id]
  (try
    (db/delete-card (Integer. id))
    (response {:deleted-card-id id})
    (catch Exception e (response-error e))))


;; -------------------------
;; ROUTES


(defroutes api
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
                                wrap-json-response) id)))

(defroutes app
  (GET "/" [] index-handler)

  (context "/api" [] api)

  (context "/card" [] index-handler)

  (resources "/")

  (not-found (view/not-found)))


;; -------------------------
;; SERVER


;; (defn my-app [_res] {:status 200 :body "Hello!"})


(defn start-server [& [port]]
  (let [port (Integer. (or port (env "PORT") 3000))]
    (webserver/run-jetty app {:port port})))

(defn -main [& args]
  (db/migrate-up)
  (start-server args))