(ns client.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]))

(deftest test-component
  (testing "div is ok"
    ;; (rdom/render [:div "ok"] (.getElementById js/document "app"))
    (is (= 1 1))))

(deftest fake-test
  (testing "fake description"
    (is (= 1 1))))