(ns ring.middleware.honeybadger-test
  (:use clojure.test
        ring.middleware.honeybadger))

(defn erroring-handler [request]
  (throw (Exception. "Something went wrong")))

(deftest test-wrap-honeybadger
  (let [handler (wrap-honeybadger erroring-handler)]
    (handler {})))
