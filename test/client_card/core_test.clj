(ns client-card.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [client-card.core :as core]
            [client-card.db :as db]
            [clojure.string :as string]))

(def cards [{:id 4
             :full_name "Petr Alekseev"
             :gender "Male"
             :address "Egorova, 10"
             :birthday "1989-08-12"
             :id_policy 12002334421}
            {:id 5
             :full_name "Irina Savchenko"
             :gender "Female"
             :address "Egorova, 12"
             :birthday "1991-08-12"
             :id_policy 99812234700}])

(defn get-card [id] (filter #(= (:id %) id) cards))

(deftest index-handler
  (testing "Is correctly response"
    (with-redefs [db/get-all-cards (fn [] cards)]
      (let [{:keys [status headers body]} (core/index-handler {})]
        (is (= status 200))
        (is (= headers {"Content-Type" "text/html"}))

        (is (string/includes? body (:full_name (first cards))))
        (is (string/includes? body (:address (first cards))))
        (is (string/includes? body (:birthday (first cards))))
        (is (string/includes? body (str (:id_policy (first cards)))))

        (is (string/includes? body (:full_name (second cards))))
        (is (string/includes? body (:address (second cards))))
        (is (string/includes? body (:birthday (second cards))))
        (is (string/includes? body (str (:id_policy (second cards)))))))))

(deftest card-view-handler
  (testing "Is correctly response"
    (with-redefs [db/get-card (fn [id] (get-card id))]
      (let [{:keys [status headers body]} (core/card-view-handler 4)]
        (is (= status 200))
        (is (= headers {"Content-Type" "text/html"}))

        (is (string/includes? body (:full_name (first cards))))
        (is (string/includes? body (:address (first cards))))
        (is (string/includes? body (:birthday (first cards))))
        (is (string/includes? body (str (:id_policy (first cards)))))

        (is (not (string/includes? body (:full_name (second cards)))))))))

(deftest card-edit-handler
  (testing "Is correctly response"
    (with-redefs [db/get-card (fn [id] (get-card id))]
      (let [{:keys [status headers body]} (core/card-edit-handler 5)]
        (is (= status 200))
        (is (= headers {"Content-Type" "text/html"}))

        (is (string/includes? body (:full_name (second cards))))
        (is (string/includes? body (:address (second cards))))
        (is (string/includes? body (:birthday (second cards))))
        (is (string/includes? body (str (:id_policy (second cards)))))

        (is (not (string/includes? body (:full_name (first cards)))))))))