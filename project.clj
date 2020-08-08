(defproject client-card "0.1.0-SNAPSHOT"
  :description "Demo CRUD app"
  :url "https://github.com/kakoi-to-pirat/crud-clojure"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.1"]
                 [compojure "1.6.1"]
                 [lynxeyes/dotenv "1.0.2"]
                 [lein-githooks "0.1.0"]
                 [lein-cljfmt "0.6.8"]
                 [hiccup "1.0.5"]]
  :repl-options {:init-ns client-card.core}
  :main client-card.core
  :min-lein-version "2.5.0"
  :uberjar-name "client-card.jar"
  :aot :all
  :profiles {:dev
             {:main client-card.core/-dev-main
              :plugins [[lein-githooks "0.1.0"]
                        [lein-cljfmt "0.6.8"]]
              :githooks {:auto-install false
                         :pre-push ["lein uberjar"]
                         :pre-commit ["lein check" "lein cljfmt check" "lein test"]}}})
