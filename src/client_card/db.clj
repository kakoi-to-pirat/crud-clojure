(ns client-card.db
  (:require [clojure.java.jdbc :as sql]
            [dotenv :refer [env]]
            [clojure.string :as string]))


;; -------------------------
;; CONFIG


(def db-spec-dev {:dbtype (env "DBTYPE")
                  :name (env "NAME")
                  :subprotocol (env "SUBPROTOCOL")
                  :host (env "HOST")
                  :subname (env "SUBNAME")
                  :user (env "USER")
                  :password (env "USER")})

(def db-uri
  (java.net.URI. (or (System/getenv "DATABASE_URL") "")))

(def user-and-password
  (if (nil? (.getUserInfo db-uri))
    nil (string/split (.getUserInfo db-uri) #":")))

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :user (get user-and-password 0)
              :password (get user-and-password 1)
              :subname (if (= -1 (.getPort db-uri))
                         (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                         (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))})

(defn get-db-spec [] (if (env "DEV") db-spec-dev db-spec))


;; -------------------------
;; DDL


(def cards-table-ddl (sql/create-table-ddl :cards
                                           [[:id "SERIAL PRIMARY KEY"]
                                            [:full_name "VARCHAR(100)"]
                                            [:gender "VARCHAR(10)"]
                                            [:address "VARCHAR(100)"]
                                            [:birthday :date]
                                            [:id_policy "BIGINT UNIQUE"]]))


;; -------------------------
;; MIGRATIONS


(defn create-cards-table []
  (sql/db-do-commands (get-db-spec) cards-table-ddl)
  (println "Client cards table was created is successful"))

(defn drop-cards-table []
  (sql/db-do-commands (get-db-spec) [(sql/drop-table-ddl :cards)])
  (println "Client cards table was removed is successful"))


;; -------------------------
;; APPLY MIGRATIONS


(defn db-schema-migrated? [table-name]
  (-> (sql/query (get-db-spec)
                 ["SELECT count (*) 
                   FROM information_schema.tables
                   WHERE table_name = ?" table-name])
      first :count pos?))

(defn apply-migrations []
  (if-not (db-schema-migrated? "cards")
    (create-cards-table)
    (println "Migrations already was apllyed")))

(defn undo-migrations []
  (if (db-schema-migrated? "cards")
    (drop-cards-table)
    (println "Migrations already was rolled back")))


;; -------------------------
;; QUERY OF CARDS


(defn get-all-cards []
  (sql/query (get-db-spec) ["SELECT * FROM cards"]))

(defn get-card [id]
  (sql/query (get-db-spec) ["SELECT * FROM cards WHERE id = ?" id]))

(defn create-card [data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! (get-db-spec) ["INSERT INTO 
                            cards (full_name, gender, address, birthday, id_policy) 
                            VALUES (?, ?, ?, ?::date, ?::bigint)"
                                 full_name gender address birthday id_policy])))

(defn update-card [id data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! (get-db-spec) ["UPDATE public.cards 
                            SET full_name = ?, gender = ?, address = ?, birthday = ?::date, id_policy = ?::bigint
                            WHERE id = ?::integer"
                                 full_name gender address birthday id_policy id])))

(defn delete-card [id]
  (sql/execute! (get-db-spec) ["DELETE FROM cards WHERE id = ?" id]))