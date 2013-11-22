(ns ring.middleware.honeybadger
  (:require [clj-stacktrace.core :as st]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [ring.util.request :as req]))

(def endpoint
  "https://api.honeybadger.io/v1/notices")

(def hostname
  (.getHostName (java.net.InetAddress/getLocalHost)))

(def current-dir
  (.getCanonicalPath (io/file ".")))

(defn honeybadger-map [ex request options]
  {:notifier
   {:name "Ring Honeybadger Middleware"
    :url "https://github.com/weavejester/ring-honeybadger"
    :version "1.3.0"
    :language "clojure"}
   
   :error
   {:class     (.getName (:class ex))
    :message   (:message ex)
    :backtrace (for [trace (:trace-elems ex)]
                 {:number (:line trace)
                  :file   (:file trace)
                  :method (or (:method trace) (:fn trace))})}

   :request
   {:url     (req/request-url request)
    :params  (merge (:query-params request) (:form-params request))
    :session (:session request)
    :context (::context request)}

   :server
   {:project_root     {:path current-dir}
    :environment_name (:env options "development")
    :hostname         hostname}})

(defn send-exception! [ex request options]
  (http/post endpoint {:content-type :json
                       :accept :json
                       :headers {"X-API-Key" (:api-key options)}
                       :body (json/generate-string
                              (honeybadger-map ex request options))}))

(defn wrap-honeybadger [handler options]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (send-exception! (st/parse-exception t) request options)
        (throw t)))))
