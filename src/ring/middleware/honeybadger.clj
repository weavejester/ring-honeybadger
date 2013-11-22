(ns ring.middleware.honeybadger
  (:require [clj-stacktrace.core :as st]))

(defn send-exception! [ex]
  (prn ex))

(defn wrap-honeybadger [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (send-exception! (st/parse-exception t))
        (throw t)))))
