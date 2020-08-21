(defproject medical-cards "0.1.0-SNAPSHOT"
  :description "Demo CRUD app"
  :url "https://github.com/kakoi-to-pirat/crud-clojure"

  :dependencies [[cljs-ajax "0.8.0"]
                 [compojure "1.6.1"]
                 [environ "1.2.0"]
                 [hiccup "1.0.5"]
                 [lein-cljfmt "0.6.8"]
                 [lein-githooks "0.1.0"]
                 [lynxeyes/dotenv "1.0.2"]
                 [metosin/reitit "0.5.1"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.764" :scope "provided"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.14"]
                 [pez/clerk "1.0.0"]
                 [re-frame "1.0.0"]
                 [reagent "0.10.0"]
                 [reagent-utils "0.3.3"]
                 [ring "1.8.1"]
                 [ring/ring-json "0.5.0"]
                 [venantius/accountant "0.2.5" :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.2.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.4.6" :exclusions [org.clojure/clojure]]]

  :resource-paths ["resources" "target/cljsbuild"]

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :dev :compiler :output-dir]
                                    [:cljsbuild :builds :dev :compiler :output-to]]

  :minify-assets [[:css {:source "resources/public/app.css"
                         :target "resources/public/app.min.css"}]]

  :cljsbuild {:builds {:dev {:figwheel {:on-jsload "client.core/mount-root"
                                        :open-urls ["http://localhost:3449/"]}

                             :source-paths ["src/client_card/client" "env/dev"]

                             :compiler {:main "client.dev"
                                        :asset-path "/js/out"
                                        :output-to  "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :source-map true
                                        :optimizations :none
                                        :pretty-print  true}}

                       :prod {:source-paths ["src/client_card/client" "env/prod"]

                              :compiler {:output-to  "target/cljsbuild/public/js/app.js"
                                         :output-dir "target/cljsbuild/public/js"
                                         :source-map "target/cljsbuild/public/js/app.js.map"
                                         :optimizations :advanced
                                         :infer-externs true
                                         :pretty-print  false}}}}

  :figwheel {:css-dirs ["resources/public"]
             :ring-handler client-card.server.core/app}

  :repl-options {:init-ns client-card.server.core}
  :uberjar-name "client-card.jar"
  :main client-card.server.core
  :min-lein-version "2.5.0"

  :profiles {:dev {:dependencies [[binaryage/devtools "1.0.2"]]

                   :source-paths ["env/dev"]

                   :plugins [[lein-githooks "0.1.0"]
                             [lein-cljfmt "0.6.8"]
                             [lein-figwheel "0.5.20"]]

                   :githooks {:auto-install false
                              :pre-push ["lein uberjar"]
                              :pre-commit ["lein check" "lein cljfmt check" "lein test"]}

                   :env {:environment "development"}}

             :test {:env {:environment "test"}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["src/client_card/client" "env/prod"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "prod"]]
                       :aot :all
                       :omit-source true}})
