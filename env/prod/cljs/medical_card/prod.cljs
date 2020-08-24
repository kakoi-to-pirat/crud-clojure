(ns medical-card.prod
  (:require [medical-card.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
