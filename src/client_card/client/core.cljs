(ns client.core)

(defn log [& args] (apply (.-log js/console) args))

(log "Start ok!")