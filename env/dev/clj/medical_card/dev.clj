(ns medical-card.dev
  (:require [ring.adapter.jetty :as webserver]
            [ring.middleware.reload :refer [wrap-reload]]
            [dotenv :refer [env]]
            [medical-card.core :as core]
            [medical-card.db :as db]
            [figwheel-sidecar.repl-api :as ra]))


;; -------------------------
;; SERVER


(defonce server (atom nil))

(defn start-server [& [port]]
  (let [port (Integer. (or (:port port) (env "APP_PORT") 3000))]
    (reset! server (webserver/run-jetty (wrap-reload #'core/app)
                                        {:port port :join? false}))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(defn start-fw []
  (ra/start-figwheel!))

(defn stop-fw []
  (ra/stop-figwheel!))

(defn cljs []
  (ra/cljs-repl))

(defn -main-dev []
  (db/migrate-up)
  (start-server))


;; -------------------------
;; REPL


(comment
  (cljs)
  (start-fw)
  (stop-fw)
  (start-server)
  (stop-server))