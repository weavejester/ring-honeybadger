(ns ring.middleware.honeybadger-test
  (:use clojure.test
        ring.middleware.honeybadger)
  (:require [ring.mock.request :as mock]
            [clj-http.fake :as fake]
            [cheshire.core :as json]))

(defn erroring-handler [request]
  (throw (Exception. "Something went wrong")))

(def sent-data (atom []))

(defn fake-endpoint [request]
  (swap! sent-data conj (-> request :body .getContent slurp (json/parse-string true)))
  {:status 200, :headers {}, :body ""})

(defmacro with-fake-endpoint [& body]
  `(fake/with-fake-routes {endpoint fake-endpoint} ~@body))

(deftest test-wrap-honeybadger
  (let [handler (wrap-honeybadger erroring-handler {:api-key "XXXXXXX"})]
    (with-fake-endpoint
      (is (thrown? Throwable (handler (mock/request :get "/"))))
      (let [data (first @sent-data)]
        (testing "data exists"
          (is (map? (:notifier data)))
          (is (map? (:error data)))
          (is (map? (:request data)))
          (is (map? (:server data))))

        (testing "notifier"
          (is (= (get-in data [:notifier :name]) "Ring Honeybadger Middleware"))
          (is (= (get-in data [:error :class])   "java.lang.Exception"))
          (is (= (get-in data [:error :message]) "Something went wrong")))))))
