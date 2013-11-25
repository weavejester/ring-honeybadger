(defproject ring-honeybadger "0.1.0-SNAPSHOT"
  :description "Ring middleware for logging exceptions to honeybadger.io"
  :url "https://github.com/weavejester/ring-honeybadger"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.7"]
                 [clj-stacktrace "0.2.7"]
                 [ring/ring-core "1.2.1"]]
  :profiles
  {:dev {:dependencies [[clj-http-fake "0.4.1"]
                        [ring-mock "0.1.5"]]}})
