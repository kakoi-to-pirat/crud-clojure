(ns client-card.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [client-card.core :as core]
            [client-card.db :as db]
            ;; [clojure.string :as string]
            ;; [mock-clj.core :as mc]
            ;; [client-card.config :as config]
            [clojure.data.json :as json]))

(def cards [{:id 1
             :full_name "Petr Alekseev"
             :gender "Male"
             :address "Egorova, 17"
             :birthday "1989-08-10"
             :id_policy 12002334421}
            {:id 2
             :full_name "Irina Savchenko"
             :gender "Female"
             :address "Egorova, 25"
             :birthday "1991-08-10"
             :id_policy 99812234700}])

(def errDatabaseException "Error in database!")

(defn get-card [id] (filter #(= (:id %) id) cards))

(defn database-for-tests [all-tests]
  (db/migrate-down)
  (db/migrate-up)
  (doseq [c cards] (db/create-card c))
  (all-tests))

(use-fixtures :once database-for-tests)

(deftest index-route
  (testing "Is correctly response"
    (let [{:keys [status headers body]} (core/app {:uri "/" :request-method :get})
          cards-from-response (json/read-str body :key-fn keyword)]
      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= cards-from-response cards)))))

(deftest card-view-route
  (testing "Is correctly response"
    (let [{:keys [status headers body]} (core/app {:uri (format "/card/%s" (:id (first cards)))
                                                   :request-method :get})
          cards-from-response (json/read-str body :key-fn keyword)]
      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= cards-from-response [(first cards)])))))

;; (deftest card-view-handler
;;   (testing "Is correctly response"
;;     (with-redefs [db/get-card (fn [id] (get-card id))]
;;       (let [{:keys [status headers body]} (core/card-view-handler 4)]
;;         (is (= status 200))
;;         (is (= headers {"Content-Type" "text/html"}))

;;         (is (string/includes? body (:full_name (first cards))))
;;         (is (string/includes? body (:address (first cards))))
;;         (is (string/includes? body (:birthday (first cards))))
;;         (is (string/includes? body (str (:id_policy (first cards)))))

;;         (is (not (string/includes? body (:full_name (second cards)))))))))

;; (deftest card-edit-handler
;;   (testing "Is correctly response"
;;     (with-redefs [db/get-card (fn [id] (get-card id))]
;;       (let [{:keys [status headers body]} (core/card-edit-handler 5)]
;;         (is (= status 200))
;;         (is (= headers {"Content-Type" "text/html"}))

;;         (is (string/includes? body (:full_name (second cards))))
;;         (is (string/includes? body (:address (second cards))))
;;         (is (string/includes? body (:birthday (second cards))))
;;         (is (string/includes? body (str (:id_policy (second cards)))))

;;         (is (not (string/includes? body (:full_name (first cards)))))))))

;; (deftest card-update-handler
;;   (testing "Was called db/update-card with correctly params"
;;     (mc/with-mock [db/update-card]
;;       (let [card (first cards)]
;;         (core/card-update-handler {:params card})
;;         (is (= (mc/calls db/update-card) [[(:id card) card]])))))

;;   (testing "Is rendered Exception"
;;     (with-redefs [db/update-card (fn [id data] (throw (Exception. (format "%s %s %s" errDatabaseException id data))))]
;;       (let [card (first cards)
;;             {:keys [status headers body]} (core/card-update-handler {:params card})]
;;         (is (= status 500))
;;         (is (= headers {"Content-Type" "text/html"}))
;;         (is (string/includes? body (format "%s %s %s" errDatabaseException (:id card) card)))))))

;; (deftest card-save-handler
;;   (testing "Was called db/update-card with correctly params"
;;     (mc/with-mock [db/create-card]
;;       (let [card (first cards)]
;;         (core/card-save-handler {:params card})
;;         (is (= (mc/calls db/create-card) [[card]])))))

;;   (testing "Is rendered exception"
;;     (with-redefs [db/create-card (fn [data] (throw (Exception. (format "%s %s" errDatabaseException data))))]
;;       (let [card (first cards)
;;             {:keys [status headers body]} (core/card-save-handler {:params card})]
;;         (is (= status 500))
;;         (is (= headers {"Content-Type" "text/html"}))
;;         (is (string/includes? body (format "%s %s" errDatabaseException card)))))))

;; (deftest card-delete-handler
;;   (testing "Was called db/update-card with correctly params"
;;     (mc/with-mock [db/delete-card]
;;       (let [id (:id (first cards))]
;;         (core/card-delete-handler id)
;;         (is (= (mc/calls db/delete-card) [[id]])))))

;;   (testing "Is rendered exception"
;;     (with-redefs [db/delete-card (fn [id] (throw (Exception. (format "%s %s" errDatabaseException id))))]
;;       (let [id (:id (first cards))
;;             {:keys [status headers body]} (core/card-delete-handler id)]
;;         (is (= status 500))
;;         (is (= headers {"Content-Type" "text/html"}))
;;         (is (string/includes? body (format "%s %s" errDatabaseException id)))))))))