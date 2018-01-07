(ns demoaws.lambda
  (:require  [clojure.data.json :as json]
             [clojure.string :as s]
             [clojure.java.io :as io]))


(defn- key->keyword [key-string]
  (-> key-string
      (s/replace #"([a-z])([A-Z])" "$1-$2")
      (s/replace #"([A-Z]+)([A-Z])" "$1-$2")
      (s/lower-case)
      (keyword)))

(defn stream->edn [in]
  (json/read (io/reader in)))

(defn edn->stream [edn out]
  (let [w (io/writer out)]
    (json/write edn w)
    (.flush w)))

(defmacro gen-lambda
  "Create a named class that can be invoked as an AWS Lambda function."
  [handler {:keys [name simple]}]
  (let [prefix (gensym)
        handleRequestMethod (symbol (str prefix "handleRequest"))]
    `(do
       (gen-class
        :name ~name
        :prefix ~prefix
        :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
       ~(if (false? simple)
          `(def ~handleRequestMethod ~handler)
          `(defn ~handleRequestMethod
             [this# in# out# ctx#]
             (-> (stream->edn in#)
                 (~handler ctx#)
                 (edn->stream out#)))))))
