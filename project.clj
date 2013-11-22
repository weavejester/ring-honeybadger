(defproject ring-honeybadger "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.7"]
                 [clj-stacktrace "0.2.7"]]
  :profiles
  {:dev {:dependencies [[clj-http-fake "0.4.1"]]}})
