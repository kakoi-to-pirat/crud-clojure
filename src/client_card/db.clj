(ns client-card.db
  (:require [clojure.java.jdbc :as sql]
            [client-card.config :as config]))


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
  (sql/db-do-commands (config/db-spec) cards-table-ddl)
  (println "Client cards table was created is successful"))

(defn drop-cards-table []
  (sql/db-do-commands (config/db-spec) [(sql/drop-table-ddl :cards)])
  (println "Client cards table was removed is successful"))


;; -------------------------
;; APPLY MIGRATIONS


(defn db-schema-migrated? [table-name]
  (-> (sql/query (config/db-spec)
                 ["SELECT count (*) 
                   FROM information_schema.tables
                   WHERE table_name = ?" table-name])
      first :count pos?))

(defn migrate-up []
  (if-not (db-schema-migrated? "cards")
    (create-cards-table)
    (println "Migrations already was apllyed")))

(defn migrate-down []
  (if (db-schema-migrated? "cards")
    (drop-cards-table)
    (println "Migrations already was rolled back")))


;; -------------------------
;; QUERY OF CARDS


(defn get-all-cards []
  (sql/query (config/db-spec) ["SELECT * FROM cards"]))

(defn get-card [id]
  (sql/query (config/db-spec) ["SELECT * FROM cards WHERE id = ?" id]))

(defn create-card [data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! (config/db-spec) ["INSERT INTO 
                            cards (full_name, gender, address, birthday, id_policy) 
                            VALUES (?, ?, ?, ?::date, ?::bigint)"
                                    full_name gender address birthday id_policy])))

(defn update-card [id data]
  (let [{:keys [full_name, gender, address, birthday, id_policy]} data]
    (sql/execute! (config/db-spec) ["UPDATE public.cards 
                            SET full_name = ?, gender = ?, address = ?, birthday = ?::date, id_policy = ?::bigint
                            WHERE id = ?::integer"
                                    full_name gender address birthday id_policy id])))

(defn delete-card [id]
  (sql/execute! (config/db-spec) ["DELETE FROM cards WHERE id = ?" id]))

(defn q [sql] (sql/query (config/db-spec) [sql]))