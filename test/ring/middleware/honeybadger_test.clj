(ns ring.middleware.honeybadger-test
  (:use clojure.test
        clj-http.fake
        ring.middleware.honeybadger
        clojure.pprint))

(defn erroring-handler [request]
  (throw (Exception. "Something went wrong")))

(def sent-data (atom []))

(defn fake-honeybadger [request]
  (swap! sent-data conj (:body request))
  {:status 200, :headers {}, :body ""})

(defmacro with-fake-honeybadger [& body]
  `(with-fake-routes {endpoint fake-honeybadger}
     ~@body))

(deftest test-wrap-honeybadger
  (let [handler (wrap-honeybadger erroring-handler {})]
    (with-fake-honeybadger
      (is (thrown? Throwable (handler {})))
      (let [data (first @sent-data)]
        (is (= (get-in data [:notifier :name]) "Ring Honeybadger Middleware"))
        (is (= (get-in data [:error :class]) "java.lang.Exception"))
        (is (= (get-in data [:error :message]) "Something went wrong"))))))
