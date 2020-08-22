(ns client-card.server.prod
  (:require [ring.adapter.jetty :as webserver]
            [dotenv :refer [env]]
            [client-card.server.core :as core]
            [client-card.server.db :as db]))

(defn start-server [& [port]]
  (let [port (Integer. (or port (env "PORT") 3000))]
    (webserver/run-jetty core/app {:port port})))

(defn -main []
  (db/migrate-up)
  (start-server))