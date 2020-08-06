(defproject client-card "0.1.0-SNAPSHOT"
  :description "Demo CRUD app"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.1"]]
  :repl-options {:init-ns client-card.core}
  :main client-card.core
  :profiles {:dev
             {:main client-card.core/-dev-main}})
