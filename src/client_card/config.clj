(ns client-card.config
  (:require [environ.core :as environ]
            [clojure.string :as string]
            [dotenv :refer [env]]))


;; -------------------------
;; DATABASE CONFIG


(defonce db-env (atom nil))

(def db-spec-dev {:name (env "DB_NAME")
                  :subprotocol "postgresql"
                  :subname (format "//%s:%s/%s" (env "DB_HOST") (env "DB_PORT") (env "DB_NAME"))
                  :user (env "DB_USER")
                  :password (env "DB_PASSWORD")})

(def db-spec-test {:name (env "DB_TEST_NAME")
                   :subprotocol "postgresql"
                   :subname (format "//%s:%s/%s" (env "DB_TEST_HOST") (env "DB_TEST_PORT") (env "DB_TEST_NAME"))
                   :user (env "DB_TEST_USER")
                   :password (env "DB_TEST_PASSWORD")})

(def db-uri
  (java.net.URI. (or (System/getenv "DATABASE_URL") "")))

(def user-and-password
  (if (nil? (.getUserInfo db-uri))
    nil (string/split (.getUserInfo db-uri) #":")))

(def db-spec-production {:classname "org.postgresql.Driver"
                         :subprotocol "postgresql"
                         :user (get user-and-password 0)
                         :password (get user-and-password 1)
                         :subname (if (= -1 (.getPort db-uri))
                                    (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                                    (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))})

(defn get-db-spec [] (case (or @db-env (environ/env :environment))
                       "development" db-spec-dev
                       "test" db-spec-test
                       db-spec-production))

(defn set-db-env [env] (reset! db-env env))


;; -------------------------
;; REPL


(comment
  (set-db-env "development")
  (set-db-env "test")
  (set-db-env nil)
  (str (get-db-spec)))
