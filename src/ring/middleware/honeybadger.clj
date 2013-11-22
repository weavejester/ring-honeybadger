(ns ring.middleware.honeybadger
  (:require [clj-stacktrace.core :as st]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.java.io :as io]))

(def endpoint
  "https://api.honeybadger.io/v1/notices")

(def hostname
  (.getHostName (java.net.InetAddress/getLocalHost)))

(def current-dir
  (.getCanonicalPath (io/file ".")))

(defn honeybadger-map [ex options]
  {:notifier {:name "Ring Honeybadger Middleware"
              :url "https://github.com/weavejester/ring-honeybadger"
              :version "1.3.0"}
   :error {:class     (.getName (:class ex))
           :message   (:message ex)
           :backtrace (for [t (:trace-elems ex)]
                        {:number (:line t)
                         :file   (:file t)
                         :method (or (:method t) (:fn t))})}
   :server {:project_root     {:path current-dir}
            :environment_name (:env options "development")
            :hostname         hostname}})

(defn send-exception! [ex options]
  (http/post endpoint {:content-type :json
                       :accept :json
                       :save-request? true
                       :debug-body true
                       :headers {"X-API-Key" (:api-key options)}
                       :body (json/generate-string (honeybadger-map ex options))}))

(defn wrap-honeybadger [handler options]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (send-exception! (st/parse-exception t) options)
        (throw t)))))
