(ns client-card.db
  (:require [clojure.java.jdbc :as sql]))


;; -------------------------
;; CONFIG


(def db-spec {:dbtype "postgresql"
              :name "postgres"
              :subprotocol "postgresql"
              :host "localhost"
              :subname "//localhost:5432/postgres"
              :user "postgres"
              :password "postgres"})


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
  (sql/db-do-commands db-spec cards-table-ddl)
  (println "Client cards table was created is sucssesful"))

(defn drop-cards-table []
  (sql/db-do-commands db-spec [(sql/drop-table-ddl :cards)])
  (println "Client cards table was removed is sucssesful"))


;; -------------------------
;; APPLY MIGRATIONS


(defn db-schema-migrated? [table-name]
  (-> (sql/query db-spec
                 ["SELECT count (*) 
                   FROM information_schema.tables
                   WHERE table_name = ?" table-name])
      first :count pos?))

(defn apply-migrations []
  (if-not (db-schema-migrated? "cards")
    (create-cards-table)
    (println "Migarations already was apllyed")))

(defn undo-migrations []
  (if (db-schema-migrated? "cards")
    (drop-cards-table)
    (println "Migarations already was rolled back")))


;; -------------------------
;; QUERY OF CARDS


(defn get-all-cards []
  (sql/query db-spec ["SELECT * FROM cards"]))

(defn get-card [id]
  (sql/query db-spec ["SELECT * FROM cards WHERE id = ?" id]))

(defn create-card [data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! db-spec ["INSERT INTO 
                            cards (full_name, gender, address, birthday, id_policy) 
                            VALUES (?, ?, ?, ?::date, ?::bigint)"
                           full_name gender address birthday id_policy])))

(defn update-card [id data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! db-spec ["UPDATE public.cards 
                            SET full_name = ?, gender = ?, address = ?, birthday = ?::date, id_policy = ?::bigint
                            WHERE id = ?::integer"
                           full_name gender address birthday id_policy id])))

(defn delete-card [id]
  (sql/execute! db-spec ["DELETE FROM cards WHERE id = ?" id]))