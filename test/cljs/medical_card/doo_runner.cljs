(ns medical-card.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [medical-card.core-test]))

(doo-tests 'medical-card.core-test)