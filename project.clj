(defproject client-card "0.1.0-SNAPSHOT"
  :description "Demo CRUD app"
  :url "https://github.com/kakoi-to-pirat/crud-clojure"

  :dependencies [[cljs-ajax "0.8.0"]
                 [compojure "1.6.1"]
                 [day8.re-frame/http-fx "0.2.1"]
                 [environ "1.2.0"]
                 [hiccup "1.0.5"]
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

  :aliases {"test-cljs" [["doo" "chrome-headless" "test" "once"]]}

  :resource-paths ["resources" "target/cljsbuild"]

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :dev :compiler :output-dir]
                                    [:cljsbuild :builds :dev :compiler :output-to]]

  :minify-assets [[:css {:source "resources/public/app.css"
                         :target "resources/public/app.min.css"}]]

  :cljsbuild {:builds {:dev {:figwheel {:on-jsload "client.core/mount-root"
                                        :open-urls ["http://localhost:3449/"]}

                             :source-paths ["src/client_card/client"
                                            "env/dev/client_card/client"]

                             :compiler {:main "client.dev"
                                        :asset-path "/js/out"
                                        :output-to  "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :source-map true
                                        :optimizations :none
                                        :pretty-print  true
                                        :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                                        :preloads [day8.re-frame-10x.preload]}}

                       :prod {:source-paths ["src/client_card/client"
                                             "env/prod/client_card/client"]

                              :compiler {:output-to  "target/cljsbuild/public/js/app.js"
                                         :output-dir "target/cljsbuild/public/js"
                                         :source-map "target/cljsbuild/public/js/app.js.map"
                                         :optimizations :advanced
                                         :infer-externs true
                                         :pretty-print  false}}

                       :test {:source-paths ["src/client_card/client" "test/client_card/client"]
                              :compiler {:main client.doo-runner
                                         :asset-path "/js/out"
                                         :output-to "target/test.js"
                                         :output-dir "target/cljstest/public/js/out"
                                         :optimizations :none
                                         :pretty-print true}}}}

  :doo {:build "test"
        :alias {:default [:chrome]}}

  :figwheel {:css-dirs ["resources/public"]
             :ring-handler client-card.server.core/app}

  :min-lein-version "2.5.0"
  :main client-card.server.core

  :profiles {:dev {:main client-card.server.dev/-main-dev
                   :repl-options {:init-ns client-card.server.dev
                                  :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

                   :dependencies [[binaryage/devtools "1.0.2"]
                                  [figwheel-sidecar "0.5.16"]
                                  [nrepl "0.7.0"]
                                  [day8.re-frame/re-frame-10x "0.7.0"]
                                  [cider/piggieback "0.5.0"]]

                   :source-paths ["src" "env/dev"]

                   :plugins [[lein-githooks "0.1.0"]
                             [lein-cljfmt "0.6.8"]
                             [lein-figwheel "0.5.20"]
                             [lein-doo "0.1.10"]]

                   :githooks {:auto-install false
                              :pre-push ["lein uberjar"]
                              :pre-commit ["lein check" "lein cljfmt check" "lein test"]}

                   :env {:environment "development"}}

             :test {:env {:environment "test"}}

             :uberjar {:prep-tasks ["compile" ["cljsbuild" "once" "prod"]]
                       :uberjar-name "client-card.jar"
                       :aot :all
                       :omit-source true
                       :hooks [minify-assets.plugin/hooks]}})
