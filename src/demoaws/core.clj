(ns demoaws.core
  (:gen-class
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:require [clojure.data.json :as json]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
  (GET "/" [] "<h1>Hello World</h1>")
  (POST "/" [] "<h1>Hello POST</h1>")
  (route/not-found "<h1>Page not found</h1>"))



;; Past this is boilerplate and could be turned into a macro or lib

;; Eventually map the AWS event payload to a ring request map
(defn aws->ring
  "Maps the AWS event payload to a Ring request"
  [event]
  (let [method (:http-method event)
        port 80
        server-name (:stage event)
        remote-addr (get-in event [:request-context :caller :source-ip])
        uri (:path event)
        query-string (:query-string-parameters event)
        ;; TODO: convert this to keyword
        scheme (:protocol event)
        ;; TODO: convert this to lower-cased string as keys
        headers (:headers event)
        ;; TODO: this should be an InputStream
        body (:body event)]
    (pprint method)
    (assoc event
           :request-method (keyword (s/lower-case method))
           :server-port port
           :server-name server-name
           :remote-addr remote-addr
           :uri uri
           :query-string query-string
           :scheme scheme
           :headers headers
           :body body)))

;; handle a single event
(defn handle-event [event]
  (pprint event)
  (let [ring-req (aws->ring event)
        ring-res (app ring-req)]
    ;; Convert ring-style response to AWS Lambda Proxy response
    {:statusCode (:status ring-res)
     :headers (:headers ring-res)
     :body (:body ring-res)})
  ;; (let [first-name (:first-name event)
  ;;       last-name (:last-name event)]
  ;;   (pprint event)
  ;;   {:statusCode 200
  ;;    :headers {"Something" "===asdf"}
  ;;    :body (json/write-str {:payload (str "Yo, " first-name " " last-name)})})
  )



(defn key->keyword [key-string]
  (-> key-string
      (s/replace #"([a-z])([A-Z])" "$1-$2")
      (s/replace #"([A-Z]+)([A-Z])" "$1-$2")
      (s/lower-case)
      (keyword)))

(defn -handleRequest [this is os context]
  (let [w (io/writer os)]
    (-> (json/read (io/reader is) :key-fn key->keyword)
        (handle-event)
        (json/write w))
    (.flush w)))
