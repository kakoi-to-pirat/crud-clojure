(ns client-card.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [client-card.core :as core]
            [client-card.db :as db]
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

(def new-card {:full_name "Nikita Prokopov"
               :gender "Male"
               :address "Egorova, 8"
               :birthday "1985-08-10"
               :id_policy 112211700})

(defn database-for-tests [all-tests]
  (db/migrate-down)
  (db/migrate-up)
  (doseq [c cards] (db/create-card c))
  (all-tests))

(use-fixtures :each database-for-tests)

(deftest cards-all-view
  (testing "Is correctly response"
    (let [{:keys [status headers body]} (core/app {:uri "/card/" :request-method :get})
          cards-from-response (json/read-str body :key-fn keyword)]
      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= cards-from-response cards)))))

(deftest card-view
  (testing "Is correctly response"
    (let [{:keys [status headers body]} (core/app {:uri (format "/card/%s" (:id (first cards)))
                                                   :request-method :get})
          cards-from-response (json/read-str body :key-fn keyword)]
      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= cards-from-response [(first cards)])))))

(deftest card-update
  (testing "Card was updated"
    (let [card-id (:id (first cards))
          {:keys [status headers]} (core/app {:uri (format "/card/%s" card-id)
                                              :request-method :put
                                              :params new-card})

          {:keys [body]} (core/app {:uri (format "/card/%s" card-id)
                                    :request-method :get})

          cards-from-response (json/read-str body :key-fn keyword)
          expected-card (first cards-from-response)
          actual-card (merge {:id 1} new-card)]

      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= (count cards-from-response) 1))
      (is (= expected-card actual-card)))))

(deftest card-save
  (testing "Card was saved"
    (let [{:keys [status headers]} (core/app {:uri "/card/"
                                              :request-method :post
                                              :params new-card})

          {:keys [body]} (core/app {:uri "/card/"
                                    :request-method :get})

          cards-from-response (json/read-str body :key-fn keyword)
          expected-card (last cards-from-response)
          actual-card (merge {:id 3} new-card)]

      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= (count cards-from-response) 3))
      (is (= expected-card actual-card)))))

(deftest card-delete
  (testing "Card was deleted"
    (let [card-id (:id (first cards))
          {:keys [status headers]} (core/app {:uri (format "/card/%s" card-id)
                                              :request-method :delete})

          {:keys [body]} (core/app {:uri "/card/"
                                    :request-method :get})

          cards-from-response (json/read-str body :key-fn keyword)
          expected-card (first cards-from-response)
          actual-card (second cards)]

      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= (count cards-from-response) 1))
      (is (= expected-card actual-card)))))

(deftest not-found-route
  (testing "Is correctly response"
    (let [{:keys [status headers body]} (core/app {:uri "/not-found/" :request-method :get})
          {:keys [message]} (json/read-str body :key-fn keyword)]
      (is (= status 404))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= message "Sorry, the page you requested was not found!")))))