(ns client.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [client.page :as page]))

(def isClient (not (nil? (try (.-document js/window)
                              (catch js/Object _e nil)))))

(def rflush reagent/flush)

(defn add-test-div [_name]
  (let [doc     js/document
        body    (.-body js/document)
        div     (.createElement doc "div")]
    (.appendChild body div)
    div))

(defn with-mounted-component [comp f]
  (when isClient
    (let [div (add-test-div "_testreagent")]
      (let [_comp (rdom/render comp div #(f comp div))]
        (rdom/unmount-component-at-node div)
        (rflush)
        (.removeChild (.-body js/document) div)))))

(defn found-in [re div]
  (let [res (.-innerHTML div)]
    (if (re-find re res)
      true
      (do (println "Not found: " res)
          false))))

(deftest test-home
  (with-mounted-component (page/about-page)
    (fn [c div]
      (is (found-in #"About app" div)))))