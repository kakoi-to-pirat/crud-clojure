(defproject medical-card "0.1.0-SNAPSHOT"
  :description "Demo CRUD app"
  :url "https://github.com/kakoi-to-pirat/crud-clojure"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[cljs-ajax "0.8.0"]
                 [compojure "1.6.1"]
                 [day8.re-frame/http-fx "0.2.1"]
                 [environ "1.2.0"]
                 [hiccup "1.0.5"]
                 [lynxeyes/dotenv "1.0.2"]
                 [metosin/jsonista "0.2.6"]
                 [metosin/reitit "0.5.1"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773" :scope "provided"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.14"]
                 [pez/clerk "1.0.0"]
                 [re-frame "1.0.0"]
                 [reagent "0.10.0"]
                 [reagent-utils "0.3.3"]
                 [reagent-utils "0.3.3"]
                 [ring "1.8.1"]
                 [ring-server "0.5.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [venantius/accountant "0.2.5" :exclusions [org.clojure/tools.reader]]
                 [yogthos/config "1.1.7"]]

  :plugins [[lein-environ "1.2.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.4.6" :exclusions [org.clojure/clojure]]]

  :ring {:handler medical-card.core/app
         :uberwar-name "medical-card.war"}

  :min-lein-version "2.5.0"
  :uberjar-name "medical-card.jar"
  :main medical-card.core

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets [[:css {:source "resources/public/css/app.css"
                         :target "resources/public/css/app.min.css"}]]

  :cljsbuild {:builds {:min {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]

                             :compiler {:output-to  "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js"
                                        :source-map "target/cljsbuild/public/js/app.js.map"
                                        :optimizations :advanced
                                        :infer-externs true
                                        :pretty-print  false}}

                       :app {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                             :figwheel {:on-jsload "medical-card.core/mount-root"}

                             :compiler {:main "medical-card.dev"
                                        :asset-path "/js/out"
                                        :output-to  "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :source-map true
                                        :optimizations :none
                                        :pretty-print  true
                                        :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                                        :preloads [day8.re-frame-10x.preload]}}

                       :test {:source-paths ["src/cljs" "src/cljc" "test/cljs"]

                              :compiler {:main medical-card.doo-runner
                                         :asset-path "/js/out"
                                         :output-to "target/test.js"
                                         :output-dir "target/cljstest/public/js/out"
                                         :optimizations :none
                                         :pretty-print true}}}}

  :aliases {"test-cljs" [["doo" "chrome-headless" "test" "once"]]}

  :doo {:build "test"
        :alias {:default [:chrome]}}

  :figwheel {:http-server-root "public"
             :server-port 3449
             :nrepl-port 7002
             :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
             :css-dirs ["resources/public/css"]
             :ring-handler medical-card.core/app}

  :profiles {:dev {:repl-options {:init-ns medical-card.dev}

                   :dependencies [[binaryage/devtools "1.0.2"]
                                  [cider/piggieback "0.5.1"]
                                  [day8.re-frame/re-frame-10x "0.7.0"]
                                  [figwheel-sidecar "0.5.20"]
                                  [nrepl "0.8.0"]
                                  [pjstadig/humane-test-output "0.10.0"]
                                  [prone "2020-01-17"]
                                  [ring/ring-devel "1.8.1"]
                                  [ring/ring-mock "0.4.0"]]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.20"]
                             [lein-doo "0.1.10"]
                             [lein-githooks "0.1.0"]
                             [lein-cljfmt "0.6.8"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :githooks {:auto-install false
                              :pre-push ["lein uberjar"]
                              :pre-commit ["lein cljfmt check" "lein test" "lein doo chrome test once"]}

                   :env {:environment "development"}}

             :test {:source-paths ["src/clj" "src/cljc" "test/clj"]
                    :env {:environment "test"}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
