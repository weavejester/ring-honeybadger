(ns ring.middleware.honeybadger
  (:require [clj-stacktrace.core :as st]
            [clj-http.client :as http]))

(def endpoint
  "https://api.honeybadger.io/v1/notices")

(defn honeybadger-map [ex options]
  {:notifier {:name "Ring Honeybadger Middleware"
              :url "https://github.com/weavejester/ring-honeybadger"
              :version "1.3.0"}
   :error {:class     (.getName (:class ex))
           :message   (:message ex)
           :backtrace (for [t (:trace-elems ex)]
                        {:number (:line t)
                         :file   (:file t)
                         :method (or (:method t) (:fn t))})}})

(defn send-exception! [ex options]
  (http/post endpoint {:content-type :json
                       :accept :json
                       :as :json
                       :body (honeybadger-map ex options)}))

(defn wrap-honeybadger [handler options]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (send-exception! (st/parse-exception t) options)
        (throw t)))))
