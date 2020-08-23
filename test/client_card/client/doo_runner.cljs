(ns client.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [client.core-test]))

(doo-tests 'client.core-test)