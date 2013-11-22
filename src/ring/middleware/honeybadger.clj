(ns ring.middleware.honeybadger
  (:require [clj-stacktrace.core :as st]
            [clj-http.client :as http]))

(def endpoint
  "https://api.honeybadger.io/v1/notices")

(defn send-exception! [ex]
  (http/post endpoint {:content-type :json
                       :as :json
                       :body ex}))

(defn wrap-honeybadger [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (send-exception! (st/parse-exception t))
        (throw t)))))
