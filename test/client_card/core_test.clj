(ns client-card.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [client-card.core :as core]
            [client-card.db :as db]
            [clojure.data.json :as json]
            [client-card.fixtures :as fixtures]))

(defn database-for-tests [all-tests]
  (db/migrate-down)
  (db/migrate-up)
  (doseq [c fixtures/cards] (db/create-card c))
  (all-tests))

(use-fixtures :each database-for-tests)

(deftest cards-all-view
  (testing "Is correctly response"
    (let [{:keys [status headers body]} (core/app {:uri "/api/card/" :request-method :get})
          cards-from-response (json/read-str body :key-fn keyword)]
      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= cards-from-response fixtures/cards)))))

(deftest card-view
  (testing "Is correctly response"
    (let [{:keys [status headers body]} (core/app {:uri (format "/api/card/%s" (:id (first fixtures/cards)))
                                                   :request-method :get})
          cards-from-response (json/read-str body :key-fn keyword)]
      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= cards-from-response [(first fixtures/cards)])))))

(deftest card-update
  (testing "Card was updated"
    (let [card-id (:id (first fixtures/cards))
          {:keys [status headers]} (core/app {:uri (format "/api/card/%s" card-id)
                                              :request-method :put
                                              :params fixtures/new-card})

          {:keys [body]} (core/app {:uri (format "/api/card/%s" card-id)
                                    :request-method :get})

          cards-from-response (json/read-str body :key-fn keyword)
          expected-card (first cards-from-response)
          actual-card (merge {:id 1} fixtures/new-card)]

      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= (count cards-from-response) 1))
      (is (= expected-card actual-card)))))

(deftest card-save
  (testing "Card was saved"
    (let [{:keys [status headers]} (core/app {:uri "/api/card/"
                                              :request-method :post
                                              :params fixtures/new-card})

          {:keys [body]} (core/app {:uri "/api/card/"
                                    :request-method :get})

          cards-from-response (json/read-str body :key-fn keyword)
          expected-card (last cards-from-response)
          actual-card (merge {:id 3} fixtures/new-card)]

      (is (= status 200))
      (is (= headers {"Content-Type" "application/json; charset=utf-8"}))
      (is (= (count cards-from-response) 3))
      (is (= expected-card actual-card)))))

(deftest card-delete
  (testing "Card was deleted"
    (let [card-id (:id (first fixtures/cards))
          {:keys [status headers]} (core/app {:uri (format "/api/card/%s" card-id)
                                              :request-method :delete})

          {:keys [body]} (core/app {:uri "/api/card/"
                                    :request-method :get})

          cards-from-response (json/read-str body :key-fn keyword)
          expected-card (first cards-from-response)
          actual-card (second fixtures/cards)]

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