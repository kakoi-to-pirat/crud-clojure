(ns client-card.db
  (:require [clojure.java.jdbc :as sql]))


;; CONFIG


(def db-spec {:dbtype "postgresql"
              :name "postgres"
              :subprotocol "postgresql"
              :host "localhost"
              :subname "//localhost:5432/postgres"
              :user "postgres"
              :password "postgres"})


;; DDL


(def cards-table-ddl (sql/create-table-ddl :cards
                                           [[:id "SERIAL PRIMARY KEY"]
                                            [:full_name "varchar(100)"]
                                            [:gender "varchar(30)"]
                                            [:address "varchar(100)"]
                                            [:birthday :timestamp]
                                            [:id_policy "integer"]]))


;; MIGRATIONS


(defn db-schema-migrated? []
  (-> (sql/query db-spec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='cards'")])
      first :count pos?))

(defn create-users-table []
  (sql/db-do-commands db-spec cards-table-ddl)
  (println "Users table was created is sucssesful"))

(defn drop-users-table []
  (sql/db-do-commands db-spec [(sql/drop-table-ddl :cards)])
  (println "Users table was removed is sucssesful"))


;; QUERY OF CARDS


(defn get-all-cards []
  (sql/query db-spec ["SELECT * FROM cards"]))

(defn get-card [id]
  (sql/query db-spec ["SELECT * FROM cards WHERE id = ?" id]))

(defn create-card [data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! db-spec ["INSERT INTO 
                            cards (full_name, gender, address, birthday, id_policy) 
                            VALUES (?, ?, ?, ?::timestamp, ?)"
                           full_name gender address birthday id_policy])))

(defn update-card [id data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! db-spec ["UPDATE public.cards 
                            SET full_name = ?, gender = ?, address = ?, birthday = ?::timestamp, id_policy = ?
                            WHERE id = ?"
                           full_name gender address birthday id_policy id])))

(defn delete-card [id]
  (sql/execute! db-spec ["DELETE FROM cards WHERE id = ?" id]))