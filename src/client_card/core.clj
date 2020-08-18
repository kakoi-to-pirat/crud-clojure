(ns client-card.core
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.response :refer [redirect]]
            [dotenv :refer [env]]
            [environ.core :as environ]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found resources]]
            [client-card.db  :as db]
            [client-card.views :as views]))


;; -------------------------
;; HANDLERS


(defn index-handler [request]
  (try {:status 200
        :headers {"Content-Type" "text/html"}
        :body (views/index-view (vec (db/get-all-cards)))}
       (catch Exception e {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (views/error-view (.getMessage e))})))

(defn card-view-handler [id]
  (try {:status 200
        :headers {"Content-Type" "text/html"}
        :body (views/card-view (first (db/get-card (Integer. id))))}
       (catch Exception e {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (views/error-view (.getMessage e))})))

(defn card-edit-handler [id]
  (try {:status 200
        :headers {"Content-Type" "text/html"}
        :body (views/card-form-view (first (db/get-card (Integer. id))))}
       (catch Exception e {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (views/error-view (.getMessage e))})))

(defn card-add-handler []
  (try {:status 200
        :headers {"Content-Type" "text/html"}
        :body (views/card-form-view)}
       (catch Exception e {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (views/error-view (.getMessage e))})))

(defn card-update-handler [request]
  (let [{:keys [params]} request
        {:keys [id]} params]
    (try (db/update-card (Integer. id) params)
         (redirect "/")
         (catch Exception e {:status 500
                             :headers {"Content-Type" "text/html"}
                             :body (views/error-view (.getMessage e))}))))

(defn card-save-handler [request]
  (let [{:keys [params]} request]
    (try (db/create-card params)
         (redirect "/")
         (catch Exception e {:status 500
                             :headers {"Content-Type" "text/html"}
                             :body (views/error-view (.getMessage e))}))))

(defn card-delete-handler [id]
  (try (db/delete-card (Integer. id))
       (redirect "/")
       (catch Exception e {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (views/error-view (.getMessage e))})))

(def not-found-handler (views/not-found-view))


;; -------------------------
;; ROUTES


(defroutes app
  (GET "/" [] index-handler)
  (GET "/card/:id" [id] (card-view-handler id))
  (GET "/card/edit/:id" [id] (card-edit-handler id))
  (GET "/card/add/" [] (card-add-handler))

  (POST "/card/save/" [] (wrap-params (wrap-keyword-params card-save-handler)))
  (POST "/card/update/:id" [] (wrap-params (wrap-keyword-params card-update-handler)))
  (POST "/card/delete/:id" [id] (card-delete-handler id))

  (resources "/")
  (not-found not-found-handler))


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