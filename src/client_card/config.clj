(ns client-card.config
  (:require [environ.core :as environ]
            [clojure.string :as string]
            [dotenv :refer [env]]))


;; -------------------------
;; DATABASE CONFIG


(def db-spec-dev {:dbtype (env "DBTYPE")
                  :name (env "NAME")
                  :subprotocol (env "SUBPROTOCOL")
                  :host (env "HOST")
                  :subname (env "SUBNAME")
                  :user (env "USER")
                  :password (env "USER")})

(def db-spec-test {:dbtype (env "DBTYPE_TEST")
                   :name (env "NAME_TEST")
                   :subprotocol (env "SUBPROTOCOL_TEST")
                   :host (env "HOST_TEST")
                   :subname (env "SUBNAME_TEST")
                   :user (env "USER_TEST")
                   :password (env "USER_TEST")})

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

(defn db-spec [] (case (environ/env :environment)
                   "development" db-spec-dev
                   "test" db-spec-test
                   db-spec-production))
