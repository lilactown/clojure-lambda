(ns demoaws.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [demoaws.lambda :refer [gen-lambda]]
            [demoaws.middleware :refer [ring-adapter simple-pprint-adapter]]))

(defroutes app
  (GET "/" [] "<h1>Hello World</h1>")
  (POST "/" [] "<h1>Hello POST</h1>")
  (route/not-found "<h1>Page not found</h1>"))

(def handler
  (-> app
      ring-adapter
      simple-pprint-adapter))

(gen-lambda
  handler
  {:name demoaws.core.Greet})
