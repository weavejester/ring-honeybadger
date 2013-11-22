(ns ring.middleware.honeybadger-test
  (:use clojure.test
        clj-http.fake
        ring.middleware.honeybadger))

(defn erroring-handler [request]
  (throw (Exception. "Something went wrong")))

(def honeybadger-data (atom []))

(defn fake-honeybadger [request]
  (swap! honeybadger-data conj (:body request))
  {:status 200, :headers {}, :body ""})

(defmacro with-fake-honeybadger [& body]
  `(with-fake-routes {endpoint fake-honeybadger}
     ~@body))

(deftest test-wrap-honeybadger
  (let [handler (wrap-honeybadger erroring-handler)]
    (with-fake-honeybadger
      (is (thrown? Throwable (handler {})))
      (is (= (:message (first @honeybadger-data))
             "Something went wrong")))))
